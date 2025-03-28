package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import java.util.List;

public interface DepartmentService {

  DepartmentDto create(DepartmentCreateRequest request);

  DepartmentDto update(Integer id, DepartmentUpdateRequest request);

  String delete(Integer id);

  List<DepartmentDto> getDepartment(String name, String description);

  DepartmentDto find(Integer id);

  List<DepartmentDto> getAll();

  // Entity 대신 DTO 리스트를 반환하도록 변경
  CursorPageResponseDto<DepartmentResponseDto> getDepartmentsWithCursor(
      String nameOrDescription,
      Integer idAfter,
      String cursor,
      int size,
      String sortField,
      String sortDirection);
}
