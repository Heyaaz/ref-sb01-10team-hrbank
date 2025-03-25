package com.sprint.example.sb01part2hrbankteam10ref.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDistributionDto {
  private String groupKey;
  private Long count;
  private Double percentage;

}
