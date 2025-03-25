package com.sprint.example.sb01part2hrbankteam10ref.dto.department;


import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentUpdateRequest {

  @Size(min = 1, max = 50)
  private String name;

  @Size(min = 1, max = 255)
  private String description;

  private LocalDate establishedDate;



}
