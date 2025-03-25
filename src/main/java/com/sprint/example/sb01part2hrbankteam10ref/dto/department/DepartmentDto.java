package com.sprint.example.sb01part2hrbankteam10ref.dto.department;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {

  @NotNull
  private Integer id;

  @NotNull
  private String name;

  @NotNull
  private String description;

  @NotNull
  private LocalDate establishedDate;

  private Integer employeeCount;

}
