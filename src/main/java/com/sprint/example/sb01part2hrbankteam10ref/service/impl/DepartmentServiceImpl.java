package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.DepartmentMapper;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.DepartmentUpdateMapper;
import com.sprint.example.sb01part2hrbankteam10ref.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10ref.service.DepartmentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;
  private final EmployeeRepository employeeRepository;
  private final DepartmentUpdateMapper departmentUpdateMapper;

  @Transactional
  @Override
  public DepartmentDto create(DepartmentCreateRequest request) {
    log.info("부서 생성 요청 시작: {}", request);

    try {
      // 이름 앞뒤 공백 제거
      String departmentName = request.getName();
      log.info("부서명: '{}'", departmentName);

      // 현재 DB에 있는 모든 부서명 로깅
      List<String> existingDepartments = departmentRepository.findAll()
          .stream()
          .map(Department::getName)
          .collect(Collectors.toList());
      log.info("현재 존재하는 모든 부서명: {}", existingDepartments);

      LocalDateTime establishedDate = parseLocalDateTime(request.getEstablishedDate());
      log.info("설립일 파싱 완료: {}", establishedDate);

      // 직접 DB 조회로 검증
      Optional<Department> existingDept = departmentRepository.findByNameEquals(departmentName);
      boolean exists = existingDept.isPresent();
      log.info("부서명 '{}' 중복 확인 결과(직접 조회): {}", departmentName, exists);

      // 기존 메서드도 함께 테스트
      boolean existsByMethod = departmentRepository.existsByName(departmentName);
      log.info("부서명 '{}' 중복 확인 결과(existsByName): {}", departmentName, existsByMethod);

      if (exists) {
        log.error("이미 존재하는 부서명: {}", departmentName);
        throw new RestApiException(DepartmentErrorCode.DEPARTMENT_IS_ALREADY_EXIST,
            departmentName);
      }

      Department department = Department.builder()
          .name(departmentName)
          .description(request.getDescription())
          .establishedDate(establishedDate)
          .build();
      log.info("부서 엔티티 생성 완료: {}", department);

      Department saved = departmentRepository.save(department);
      log.info("부서 저장 완료: {}", saved);

      return departmentMapper.toDto(saved);
    } catch (RestApiException e) {
      log.error("부서 생성 도중 RestApiException 발생: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("부서 생성 중 예외 발생: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Transactional
  public DepartmentDto update(Integer id, DepartmentUpdateRequest request) {
    log.info("부서 업데이트 요청: id={}, request={}", id, request);
    
    try {
      // 1. 부서 존재 확인
      Department department = departmentRepository.findById(id)
          .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
              id.toString()));
      
      log.info("부서 조회 성공: {}", department);

      // 2. 부서명 중복 확인 및 업데이트
      if (request.getName() != null) {
        String name = request.getName();
        if (!name.equals(department.getName()) && departmentRepository.existsByName(name)) {
          throw new RestApiException(DepartmentErrorCode.DUPLICATION_NAME, name);
        }
        department.updateName(name);
        log.info("부서명 업데이트: {}", name);
      }

      // 3. 설명 업데이트
      if (request.getDescription() != null) {
        department.updateDescription(request.getDescription());
        log.info("부서 설명 업데이트: {}", request.getDescription());
      }

      // 4. 설립일 업데이트
      if (request.getEstablishedDate() != null) {
        LocalDateTime date = null;
        try {
          date = parseLocalDateTime(request.getEstablishedDate().toString());
          department.updateEstablishedDate(date);
          log.info("설립일 업데이트: {}", date);
        } catch (Exception e) {
          log.error("설립일 파싱 오류: {}", request.getEstablishedDate(), e);
          throw new RestApiException(EmployeeErrorCode.INVALID_DATE, 
              "establishedDate=" + request.getEstablishedDate());
        }
      }

      // 5. 저장 및 반환
      Department updatedDepartment = departmentRepository.save(department);
      log.info("부서 업데이트 완료: {}", updatedDepartment);
      
      return departmentMapper.toDto(updatedDepartment);
    } catch (RestApiException e) {
      log.error("부서 업데이트 중 API 오류: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("부서 업데이트 중 예외 발생: {}", e.getMessage(), e);
      throw e;
    }
  }
  @Transactional
  @Override
  public String delete(Integer id) {
    Department findDepartment = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            id.toString()));

    // 부서에 속한 직원이 있는 경우 삭제 불가
    if (employeeRepository.existsByDepartmentId(id)) {
      log.error("부서에 직원이 존재합니다. id={}", id);
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_HAS_EMPLOYEE, id.toString());
    }

    if (departmentRepository.existsById(id)) {
      departmentRepository.delete(findDepartment);
    }
    return "부서가 성공적으로 삭제되었습니다.";
  }

  @Override
  public List<DepartmentDto> getDepartment(String name, String description) {
    List<Department> departments = departmentRepository.findByNameAndDescription(
        name != null ? name : "",
        description != null ? description : "");

    return departments.stream()
        .map(departmentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public DepartmentDto find(Integer id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            id.toString()));

    return departmentMapper.toDto(department);
  }

  @Override
  public List<DepartmentDto> getAll() {
    List<Department> departments = departmentRepository.findAll();

    if (departments.isEmpty()) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENTS_EMPTY, "부서가 존재하지 않습니다.");
    }

    return departments.stream()
        .map(departmentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseDto<DepartmentResponseDto> getDepartments(
      String nameOrDescription,
      Integer idAfter,
      String cursor,
      int size,
      String sortField,
      String sortDirection) {

    // 기본값 설정
    size = size <= 0 ? 10 : size;
    sortField = sortField == null || sortField.isEmpty() ? "establishedDate" : sortField;
    sortDirection = sortDirection == null || sortDirection.isEmpty() ? "asc" : sortDirection;

    // 커서에서 idAfter 추출
    if (cursor != null && !cursor.isEmpty() && idAfter == null) {
      if ("name".equals(sortField)) {
        // 이름 기반 커서
        Department dept = (Department) departmentRepository.findByName(cursor)
            .orElse(null);
        if (dept != null) {
          idAfter = dept.getId();
        }
      } else if ("establishedDate".equals(sortField)) {
        // 설립일 기반 커서 - 해당 날짜 이후의 첫 번째 부서를 찾아야 함
        try {
          LocalDateTime date = LocalDateTime.parse(cursor);
          Department dept = (Department) departmentRepository.findFirstByEstablishedDateAfterOrderById(date)
              .orElse(null);
          if (dept != null) {
            idAfter = dept.getId();
          }
        } catch (Exception e) {
          // 날짜 파싱 오류 처리
          log.warn("설립일 커서 파싱 오류: {}", cursor);
        }
      } else {
        // ID 기반 커서
        try {
          idAfter = Integer.parseInt(cursor);
        } catch (NumberFormatException e) {
          log.warn("ID 커서 파싱 오류: {}", cursor);
        }
      }
    }

    // 데이터 조회 (size + 1로 조회하여 hasNext 판단)
    List<Department> departments = departmentRepository.findDepartmentsWithCursor(
        nameOrDescription,
        idAfter,
        sortField,
        sortDirection
    );

    // 전체 개수 조회
    Long totalElements = departmentRepository.countByNameContainingOrDescriptionContaining(
        nameOrDescription != null ? nameOrDescription : "",
        nameOrDescription != null ? nameOrDescription : ""
    );

    // 페이지네이션 처리
    boolean hasNext = departments.size() > size;
    List<DepartmentResponseDto> content = departments.stream()
        .limit(size)
        .map(departmentMapper::toDto) // DepartmentDto로 변환
        .map(dto -> DepartmentResponseDto.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .establishedDate(dto.getEstablishedDate())
            .employeeCount(dto.getEmployeeCount())
            .build()) // DepartmentResponseDto로 변환
        .toList();

    String nextCursor = null;
    Integer nextIdAfter = null;
    if (hasNext && !content.isEmpty()) {
      Department lastItem = departments.get((int)Math.min(size, departments.size()) - 1);
      nextIdAfter = lastItem.getId();

      // 정렬 필드에 따라 커서 값을 다르게 설정
      if ("name".equals(sortField)) {
        nextCursor = lastItem.getName();
      } else if ("establishedDate".equals(sortField)) {
        nextCursor = lastItem.getEstablishedDate().toString();
      } else {
        nextCursor = null;
      }
    }

    return CursorPageResponseDto.<DepartmentResponseDto>builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextIdAfter(nextIdAfter != null ? nextIdAfter.longValue() : null)
        .size(size)
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }

  private LocalDateTime parseLocalDateTime(String dateString) {
    try {
      return LocalDate.parse(dateString).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE, "establishedDate=" + dateString);
    }
  }
}
