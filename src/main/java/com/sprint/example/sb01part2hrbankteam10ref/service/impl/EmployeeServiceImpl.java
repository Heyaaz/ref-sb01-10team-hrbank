package com.sprint.example.sb01part2hrbankteam10ref.service.impl;


import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.EmployeeMapper;
import com.sprint.example.sb01part2hrbankteam10ref.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BinaryContentRepository;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeHistoryService;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeService;
import com.sprint.example.sb01part2hrbankteam10ref.storage.BinaryContentStorage;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final EmployeeHistoryService employeeHistoryService;

  private static final Set<String> SORT_FIELDS = Set.of("name", "employeeNumber", "hireDate");

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String clientIp) {

    validateEmail(request.getEmail());

    Department department = getDepartmentOrThrow(request.getDepartmentId());
    LocalDateTime hireDate = parseLocalDateTime(request.getHireDate());
    String employeeNumber = generateEmployeeNumber(hireDate);

    BinaryContent newProfile = null;
    if (validateFile(profile)) {
      newProfile = binaryContentRepository.save(
          BinaryContent.builder()
              .name(profile.getName())
              .contentType(profile.getContentType())
              .size(BigInteger.valueOf(profile.getSize()))
              .build()
      );
      binaryContentStorage.saveProfile(newProfile.getId(), profile);
    }

    Employee newEmployee = Employee.builder()
        .name(request.getName())
        .email(request.getEmail())
        .employeeNumber(employeeNumber)
        .position(request.getPosition())
        .hireDate(hireDate)
        .department(request.getDepartmentId() != null ? department : null)
        .profileImage(newProfile)
        .status(EmployeeStatus.ACTIVE)
        .build();

//    EmployeeDto before = EmployeeMapper.toDto(Employee.builder().build());
    EmployeeDto after = EmployeeMapper.toDto(employeeRepository.save(newEmployee));

    employeeHistoryService.create(employeeNumber, ChangeType.CREATED, request.getMemo(), null, after, clientIp);

    return after;
  }

  @Override
  @Transactional
  public EmployeeDto update(Integer id, EmployeeUpdateRequest request, MultipartFile profile,
      String clientIp) {

    Employee employee = getByIdOrThrow(id);
    EmployeeDto before = EmployeeMapper.toDto(employee);

    Optional.ofNullable(request.getName()).ifPresent(employee::updateName);
    Optional.ofNullable(request.getEmail()).ifPresent(email -> {
      if (!employee.getEmail().equals(email)) {
        validateEmail(email);
        employee.updateEmail(email);
      }
    });
    Optional.ofNullable(request.getPosition()).ifPresent(employee::updatePosition);
    Optional.ofNullable(request.getHireDate()).ifPresent(hireDate -> {
      employee.updateHireDate(parseLocalDateTime(hireDate));
    });
    Optional.ofNullable(request.getStatus()).ifPresent(status -> {
      log.info("상태 변경 : {} -> {}", employee.getStatus(), status);
      employee.updateStatus(status);
      if(status == EmployeeStatus.RESIGNED){
        String departmentName = employee.getDepartment() != null ? employee.getDepartment().getName() : "없음";
        log.info("이전 부서 = {}", departmentName);
        employee.updateDepartment(null);
        log.info("부서 정보 제거 후 : {}", employee.getDepartment() != null ? employee.getDepartment().getName() : "없음");
      } else {
        // 퇴사 상태가 아닌 경우에만 부서 변경 적용
        Optional.ofNullable(request.getDepartmentId()).ifPresent(departmentId -> {
          employee.updateDepartment(getDepartmentOrThrow(departmentId));
        });
      }
    });

    // 퇴사 상태가 아닌 경우에만 부서 정보 업데이트
    if(request.getStatus() == null && employee.getStatus() != EmployeeStatus.RESIGNED) {
      Optional.ofNullable(request.getDepartmentId()).ifPresent(departmentId -> {
        employee.updateDepartment(getDepartmentOrThrow(departmentId));
      });
    }

    BinaryContent newProfile = null;
    if (validateFile(profile)) {
      newProfile = binaryContentRepository.save(
          BinaryContent.builder()
              .name(profile.getName())
              .contentType(profile.getContentType())
              .size(BigInteger.valueOf(profile.getSize()))
              .build()
      );

      Integer previousProfileImageId = null;
      if (employee.getProfileImage() != null) {
        previousProfileImageId = employee.getProfileImage().getId();
      }
      employee.updateProfileImage(newProfile);              // 프로필 업데이트
      if (previousProfileImageId != null) {
        binaryContentRepository.deleteById(previousProfileImageId);  // 기존 프로필 데이터 삭제
        binaryContentStorage.deleteProfile(previousProfileImageId);  // 로컬 데이터 삭제
      }
      binaryContentStorage.saveProfile(newProfile.getId(), profile); // 로컬 저장
    }

    EmployeeDto after = EmployeeMapper.toDto(employee);
    employeeHistoryService.create(employee.getEmployeeNumber(), ChangeType.UPDATED, request.getMemo(), before, after, clientIp);

    return after;
  }

  @Override
  public EmployeeDto getById(Integer id) {
    Employee employee = getByIdOrThrow(id);
    log.info("직원 조회 결과: id={}, 부서={}", id,
        employee.getDepartment() != null ? employee.getDepartment().getName() : "없음");
    EmployeeDto dto = EmployeeMapper.toDto(employee);
    log.info("DTO 변환 결과: id={}, 부서명={}, 부서ID={}",
        dto.getId(), dto.getDepartmentName(), dto.getDepartmentId());
    return dto;
  }

  @Override
  @Transactional
  public String deleteById(Integer id, String clientIp) {
    Employee employee = getByIdOrThrow(id);

    if (employee.getProfileImage() != null) {
      Integer previousProfileImageId = employee.getProfileImage().getId();
      employee.updateProfileImage(null);
      binaryContentRepository.deleteById(previousProfileImageId);
      binaryContentStorage.deleteProfile(previousProfileImageId);
    }

    employeeRepository.deleteById(employee.getId());
    EmployeeDto before = EmployeeMapper.toDto(employee);
    employeeHistoryService.create(employee.getEmployeeNumber(), ChangeType.DELETED, null, before, null, clientIp);

    return "직원이 성공적으로 삭제되었습니다.";
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseDto<EmployeeResponseDto> getEmployeesWithCursor(
      String nameOrEmail, String employeeNumber, String departmentName,
      String position, EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection) {

    log.info("회원 커서 페이징 조회 요청 : nameOrEmail={}, idAfter={}, cursor={}, size={}, sortField={}, sortDirection={}",
        nameOrEmail, idAfter, cursor, size, sortField, sortDirection);

    try {
      // 1. 정렬 방향 설정
      boolean isAscending = "asc".equalsIgnoreCase(sortDirection);
      log.debug("정렬 방향: {}", isAscending ? "오름차순" : "내림차순");

      // 2. 커서 처리
      Integer cursorId = null;
      if (idAfter != null) {
        cursorId = idAfter;
      } else if (cursor != null && !cursor.isEmpty()) {
        try {
          cursorId = Integer.parseInt(cursor);
        } catch (NumberFormatException e) {
          log.warn("유효하지 않은 커서 값: {}", cursor);
        }
      }

      // 3. QueryDSL을 사용하여 쿼리 생성
      List<Employee> employees = employeeRepository.findEmployeesWithCursor(
          nameOrEmail, employeeNumber, departmentName, position, status,
          hireDateFrom, hireDateTo, cursorId, cursor, size, sortField, sortDirection);

      log.debug("조회된 직원 수: {}", employees.size());

      // 4. DTO 변환
      List<EmployeeResponseDto> responseDtos = employees.stream()
          .map(employee -> {
            EmployeeDto dto = EmployeeMapper.toDto(employee);
            return EmployeeResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .departmentName(dto.getDepartmentName())
                .employeeNumber(dto.getEmployeeNumber())
                .position(dto.getPosition())
                .status(dto.getStatus())
                .hireDate(dto.getHireDate())
                .build();
          }).collect(Collectors.toList());

      // 5. 다음 커서 값 설정
      String nextCursor = null;
      Long nextIdAfterValue = null;
      boolean hasNextValue = false;

      if(!employees.isEmpty() && employees.size() >= size) {
        Employee lastEmployee = employees.get(employees.size() - 1);
        nextCursor = String.valueOf(lastEmployee.getId());
        nextIdAfterValue = Long.valueOf(lastEmployee.getId());
        hasNextValue = true;
      }

      // 6. 응답 생성
      CursorPageResponseDto<EmployeeResponseDto> response = CursorPageResponseDto.<EmployeeResponseDto>builder()
          .content(responseDtos)
          .nextCursor(nextCursor)
          .nextIdAfter(nextIdAfterValue)
          .size(responseDtos.size())
          .totalElements((long) employees.size())
          .hasNext(hasNextValue)
          .build();

      return response;

    } catch (Exception e) {
      throw e;
    }
  }

  private boolean validateFile(MultipartFile multipartFile) {
    return multipartFile != null && !multipartFile.isEmpty();
  }

  private void validateEmail(String email) {
    if (employeeRepository.existsByEmail(email)) {
      throw new RestApiException(EmployeeErrorCode.EMAIL_IS_ALREADY_EXIST,
          "email=" + email);
    }
  }

  // Get or Throw
  private Employee getByIdOrThrow(Integer id) {
    Employee employee = employeeRepository.findEmployeeByIdWithDepartment(id);
    if (employee == null) {
      throw new RestApiException(EmployeeErrorCode.EMPLOYEE_NOT_FOUND, "id=" + id);
    }
    return employee;
  }

  private Department getDepartmentOrThrow(Integer departmentId) {
    return departmentRepository.findById(departmentId)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            "departmentId=" + departmentId));
  }

  // 사번 생성
  private String generateEmployeeNumber(LocalDateTime localDateTime) {
    Integer previousId = employeeRepository.findTopByOrderByIdDesc()
        .map(Employee::getId)
        .orElse(0);

    Integer footerNumber = (previousId + 1) % 1000;
    return String.format("EMP-%d-%03d", localDateTime.getYear(), footerNumber);
  }

  private LocalDateTime parseLocalDateTime(String dateString) {
    try {
      return LocalDate.parse(dateString).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE, "hireDate=" + dateString);
    }
  }
}
