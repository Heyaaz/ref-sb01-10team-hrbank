package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeStatService {
  List<EmployeeDistributionDto> getDistribution(String groupBy, EmployeeStatus status);

  List<EmployeeTrendDto> getTrend(LocalDateTime from, LocalDateTime to, String unit);

  Long getCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
