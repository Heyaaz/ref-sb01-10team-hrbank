package com.sprint.example.sb01part2hrbankteam10ref.dto.employee;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class EmployeeDto {
  private Integer id;
  private String name;
  private String email;
  private String employeeNumber;
  private Integer departmentId;
  private String departmentName;
  private String position;
  private LocalDate hireDate;
  private EmployeeStatus status;
  private Integer profileImageId;

}
