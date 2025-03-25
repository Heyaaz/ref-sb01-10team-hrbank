package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeSearchRequest;
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
import com.sprint.example.sb01part2hrbankteam10ref.repository.specification.EmployeeSpecification;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeHistoryService;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeService;
import com.sprint.example.sb01part2hrbankteam10ref.storage.BinaryContentStorage;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
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
        .department(department)
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
    Optional.ofNullable(request.getStatus()).ifPresent(employee::updateStatus);
    Optional.ofNullable(request.getHireDate()).ifPresent(hireDate -> {
      employee.updateHireDate(parseLocalDateTime(hireDate));
    });
    Optional.ofNullable(request.getDepartmentId()).ifPresent(departmentId -> {
      employee.updateDepartment(getDepartmentOrThrow(departmentId));
    });

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
    return EmployeeMapper.toDto(getByIdOrThrow(id));
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

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseDto<EmployeeDto> getAllByQuery(EmployeeSearchRequest request) {
    // 유효성 검사
    validateSearchQuery(request);

    // 기본값 설정
    Direction getSortDirection = request.getSortDirection().equals("desc") ? Direction.DESC : Direction.ASC;

    Pageable pageable = PageRequest.of(0, request.getSize(), getSortDirection, request.getSortField());
    Specification<Employee> specification = getSpecification(request);

    Page<EmployeeDto> getAll = employeeRepository.findAll(specification, pageable).map(EmployeeMapper::toDto);

    String nextCursor = null;
    Long nextIdAfter = null;
    if (!getAll.getContent().isEmpty()) {

      EmployeeDto lastEmployee = getAll.getContent().get(getAll.getContent().size() - 1);

      if (request.equalsSortField("name")) {
        nextCursor = lastEmployee.getName();

      } else if (request.equalsSortField("employeeNumber")) {
        nextCursor = lastEmployee.getEmployeeNumber();

      } else if (request.equalsSortField("hireDate")) {
        nextCursor = lastEmployee.getHireDate().toString();
      }
      nextIdAfter = lastEmployee.getId().longValue();
    }

    return CursorPageResponseDto.<EmployeeDto>builder()
        .content(getAll.getContent())
        .nextCursor(nextCursor)
        .nextIdAfter(nextIdAfter)
        .size(getAll.getSize())
        .totalElements(getAll.getTotalElements())
        .hasNext(getAll.hasNext())
        .build();
  }

  private Specification<Employee> getSpecification(EmployeeSearchRequest request) {

    Specification<Employee> spec = ((root, query, criteriaBuilder) -> null);

    // 다음 요소 커서를 기준으로 가져오기
    String sortField = request.getSortField();
    String sortDirection = request.getSortDirection();
    String cursor = request.getCursor();
    if (request.getIdAfter() != null && cursor != null &&  cursor.isBlank()) {

      if (sortField.equals("name")) {
        if (sortDirection.equals("desc")) {
          spec = spec.and(EmployeeSpecification.lessThanName(cursor));
        } else {
          spec = spec.and(EmployeeSpecification.greaterThanName(cursor));
        }

      } else if (sortField.equals("employeeNumber")) {
        if (sortDirection.equals("desc")) {
          spec = spec.and(EmployeeSpecification.lessThanEmployeeNumber(cursor));
        } else {
          spec = spec.and(EmployeeSpecification.greaterThanEmployeeNumber(cursor));
        }

      } else if (sortField.equals("hireDate")) {
        if (sortDirection.equals("desc")) {
          spec = spec.and(EmployeeSpecification.lessThanHireDate(parseLocalDateTime(cursor)));
        } else {
          spec = spec.and(EmployeeSpecification.lessThanHireDate(parseLocalDateTime(cursor)));
        }
      }
    }

    // 이름, 이메일, 부서, 직함, 사원번호 부분 일치
    if (request.getNameOrEmail() != null && !request.getNameOrEmail().isBlank()) {
      spec = spec.and(EmployeeSpecification.likeName(request.getNameOrEmail()))
          .or(EmployeeSpecification.likeEmail(request.getNameOrEmail()));
    }

    if (request.getEmployeeNumber() != null && !request.getEmployeeNumber().isBlank()) {
      spec = spec.and(EmployeeSpecification.likeEmployeeNumber(request.getEmployeeNumber()));
    }

    if (request.getDepartmentName() != null && !request.getDepartmentName().isBlank()) {
      spec = spec.and(EmployeeSpecification.likeDepartmentName(request.getDepartmentName()));
    }

    if (request.getPosition() != null && !request.getPosition().isBlank()) {
      spec = spec.and(EmployeeSpecification.likePosition(request.getPosition()));
    }

    // 입사일 범위 조건
    if (request.getHireDateFrom() != null) {
      spec = spec.and(EmployeeSpecification.equalOrGreaterThanHireDateFrom(request.getHireDateFrom().atStartOfDay()));
    }

    if (request.getHireDateTo() != null) {
      spec = spec.and(EmployeeSpecification.equalOrLessThanHireDateTo(request.getHireDateTo().atStartOfDay()));
    }

    // 상태 조건
    if (request.getStatus() != null) {
      spec = spec.and(EmployeeSpecification.equalStatus(request.getStatus()));
    }
    return spec;
  }

  // 유효성 검사
  private void validateSearchQuery(EmployeeSearchRequest request) {
    if (!SORT_FIELDS.contains(request.getSortField())) {
      throw new RestApiException(EmployeeErrorCode.INVALID_SORT_FIELD, "sortField=" + request.getSortField());
    }
    if (request.getHireDateFrom() != null && request.getHireDateTo() != null
        && request.getHireDateFrom().isAfter(request.getHireDateTo())) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE_RANGE,
          "hireDateFrom=" + request.getHireDateFrom() + ",hireDateTo=" + request.getHireDateTo());
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
    return employeeRepository.findById(id).orElseThrow(() ->
        new RestApiException(EmployeeErrorCode.EMPLOYEE_NOT_FOUND, "id=" + id));
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
