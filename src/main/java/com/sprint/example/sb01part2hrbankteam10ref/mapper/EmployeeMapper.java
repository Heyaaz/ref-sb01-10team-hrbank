package com.sprint.example.sb01part2hrbankteam10ref.mapper;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

  public static EmployeeDto toDto(Employee employee) {
    return EmployeeDto.builder()
        .id(employee.getId())
        .name(employee.getName())
        .email(employee.getEmail())
        .employeeNumber(employee.getEmployeeNumber())
        .status(employee.getStatus())
        .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : 0)
        .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : "")
        .position(employee.getPosition())
        .hireDate(employee.getHireDate() != null ? employee.getHireDate().toLocalDate() : null)
        .profileImageId(employee.getProfileImage() != null ? employee.getProfileImage().getId() : null)
        .build();
  }

}
