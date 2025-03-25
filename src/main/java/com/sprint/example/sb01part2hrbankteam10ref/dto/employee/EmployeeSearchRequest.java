package com.sprint.example.sb01part2hrbankteam10ref.dto.employee;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchRequest {
  private String nameOrEmail;
  private String employeeNumber;
  private String departmentName;
  private String position;

  private LocalDate hireDateFrom;
  private LocalDate hireDateTo;

  private EmployeeStatus status;
  private Integer idAfter;
  private String cursor;

  private Integer size;
  private String sortField;
  private String sortDirection;

  public boolean equalsSortField(String sortField) {
    return this.getSortField().equals(sortField);
  }
}
