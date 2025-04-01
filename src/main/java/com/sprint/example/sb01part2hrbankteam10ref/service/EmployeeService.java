package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String clientIp);

  EmployeeDto update(Integer id, EmployeeUpdateRequest request, MultipartFile profile,
      String clientIp);

  String deleteById(Integer id, String clientIp);

  EmployeeDto getById(Integer id);

  CursorPageResponseDto<EmployeeResponseDto> getEmployeesWithCursor(
      String nameOrEmail, String employeeNumber, String departmentName,
      String position, EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection);
}
