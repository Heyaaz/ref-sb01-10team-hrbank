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
  @Override
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
    List<Department> departments = departmentRepository.findByNameAndDescriptionCustom(
        name != null ? name : "",
        description != null ? description : "");

    return departments.stream()
        .map(departmentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Override
  public DepartmentDto find(Integer id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            id.toString()));

    return departmentMapper.toDto(department);
  }

  @Transactional(readOnly = true)
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

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseDto<DepartmentResponseDto> getDepartmentsWithCursor(
      String nameOrDescription, 
      Integer idAfter, 
      String cursor, 
      int size, 
      String sortField, 
      String sortDirection) {
      
    log.info("부서 커서 페이징 조회 요청: nameOrDescription={}, idAfter={}, cursor={}, size={}, sortField={}, sortDirection={}", 
        nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
    
    try {
      // 1. 정렬 방향 설정
      boolean isAscending = "asc".equalsIgnoreCase(sortDirection);
      log.debug("정렬 방향: {}", isAscending ? "오름차순" : "내림차순");
      
      // 2. 커서 처리 (idAfter 파라미터가 우선)
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
      
      // 3. QueryDSL을 사용하여 부서 조회
      List<Department> departments = departmentRepository.findDepartmentsWithCursor(
          nameOrDescription, cursorId, size, sortField, isAscending);
      
      log.debug("조회된 부서 수: {}", departments.size());
      
      // 4. DTO 변환 - 수정된 부분
      List<DepartmentResponseDto> responseDtos = departments.stream()
          .map(department -> {
              DepartmentDto dto = departmentMapper.toDto(department);
              // 빌더 패턴 사용하여 모든 필드 초기화
              return DepartmentResponseDto.builder()
                  .id(dto.getId())
                  .name(dto.getName())
                  .description(dto.getDescription())
                  .establishedDate(dto.getEstablishedDate())
                  .employeeCount(dto.getEmployeeCount())
                  .build();
          })
          .collect(Collectors.toList());
      
      // 5. 다음 커서 생성
      String nextCursor = null;
      Long nextIdAfterValue = null;
      boolean hasNextValue = false;
      
      if (!departments.isEmpty() && departments.size() >= size) {
        Department lastDepartment = departments.get(departments.size() - 1);
        nextCursor = String.valueOf(lastDepartment.getId());
        nextIdAfterValue = Long.valueOf(lastDepartment.getId());
        hasNextValue = true;
      }
      
      // 6. 응답 생성 - 모든 필드 초기화
      CursorPageResponseDto<DepartmentResponseDto> response = CursorPageResponseDto.<DepartmentResponseDto>builder()
          .content(responseDtos)
          .nextCursor(nextCursor)
          .nextIdAfter(nextIdAfterValue)
          .size(size)
          .totalElements((long) responseDtos.size())
          .hasNext(hasNextValue)
          .build();
      
      log.info("부서 커서 페이징 조회 완료: 결과 수={}, 다음 커서={}", responseDtos.size(), nextCursor);
      return response;
      
    } catch (Exception e) {
      log.error("부서 커서 페이징 조회 중 오류 발생", e);
      throw e;
    }
  }

  private LocalDateTime parseLocalDateTime(String dateString) {
    try {
      return LocalDate.parse(dateString).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE, "establishedDate=" + dateString);
    }
  }
}
