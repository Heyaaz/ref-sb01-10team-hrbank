package com.sprint.example.sb01part2hrbankteam10ref.repository.custom;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeRepositoryCustom {

  List<Employee> findEmployeesWithCursor(
      String nameOrEmail, String employeeNumber, String departmentName,
      String position, EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection);

  Employee findEmployeeByIdWithDepartment(Integer id);

  long countByStatusAndHireDate(EmployeeStatus status, LocalDateTime fromDate, LocalDateTime toDate);
}
