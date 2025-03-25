package com.sprint.example.sb01part2hrbankteam10ref.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTrendDto {

  private String date;
  private Long count;
  private Long change;
  private Double changeRate;



}
