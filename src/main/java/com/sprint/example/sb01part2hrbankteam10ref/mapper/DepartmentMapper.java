package com.sprint.example.sb01part2hrbankteam10ref.mapper;

import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

  private final EmployeeHistoryRepository employeeHistoryRepository;
  private final EmployeeRepository employeeRepository;

  public DepartmentMapper(EmployeeHistoryRepository employeeHistoryRepository,
      EmployeeRepository employeeRepository) {
    this.employeeHistoryRepository = employeeHistoryRepository;
    this.employeeRepository = employeeRepository;
  }

  public DepartmentDto toDto(Department department) {
    return DepartmentDto.builder()
        .id(department.getId())
        .name(department.getName())
        .description(department.getDescription())
        .establishedDate(department.getEstablishedDate().toLocalDate())
        .employeeCount(employeeRepository.countByDepartmentId(department.getId()))
        .build();
  }
  
  public DepartmentResponseDto toResponseDto(Department department) {
    return DepartmentResponseDto.builder()
        .id(department.getId())
        .name(department.getName())
        .description(department.getDescription())
        .establishedDate(department.getEstablishedDate().toLocalDate())
        .employeeCount(employeeRepository.countByDepartmentId(department.getId()))
        .build();
  }
}
