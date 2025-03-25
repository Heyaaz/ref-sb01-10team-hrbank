package com.sprint.example.sb01part2hrbankteam10ref.dto.department;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDto {

  private Integer id;
  private String name;
  private String description;
  private LocalDate establishedDate;
  private Integer employeeCount;

}
