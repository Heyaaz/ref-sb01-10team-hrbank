package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.controller.docs.DepartmentDocs;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController implements DepartmentDocs {

  private final DepartmentService departmentService;

  // 부서 생성
  @PostMapping
  @Override
  public ResponseEntity<DepartmentDto> createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request) {

    DepartmentDto department = departmentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(department);
  }

  // 부서 수정
  @PatchMapping("/{id}")
  @Override
  public ResponseEntity<DepartmentDto> updateDepartment(
      @PathVariable Integer id,
      @Valid @RequestBody DepartmentUpdateRequest request) {

    DepartmentDto updatedDepartment = departmentService.update(id, request);
    return ResponseEntity.ok()
        .body(updatedDepartment);
  }

  // 부서 삭제
  @DeleteMapping("/{id}")
  @Override
  public ResponseEntity<String> deleteDepartment(@PathVariable Integer id) {
    return ResponseEntity.ok()
        .body(departmentService.delete(id));
  }

  // 부서 상세 조회
  @GetMapping("/{id}")
  @Override
  public ResponseEntity<DepartmentDto> getDepartment(@PathVariable Integer id) {
    DepartmentDto department = departmentService.find(id);
    return ResponseEntity.ok().body(department);
  }

  // 부서 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto<DepartmentResponseDto>> getDepartmentsWithCursor(
      @RequestParam(value = "nameOrDescription", required = false) String nameOrDescription,
      @RequestParam(value = "idAfter", required = false) Integer idAfter,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(value = "sortField", defaultValue = "establishedDate") String sortField,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {

    // 서비스 계층에서 페이징 처리 및 DTO 변환이 이루어짐
    CursorPageResponseDto<DepartmentResponseDto> response = departmentService.getDepartmentsWithCursor(
        nameOrDescription, idAfter, cursor, size, sortField, sortDirection);
    
    return ResponseEntity.ok(response);
  }
}
