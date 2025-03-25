package com.sprint.example.sb01part2hrbankteam10ref.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@AllArgsConstructor
public class EmployeeCreateRequest {
  @NotBlank(message = "이름은 필수값입니다.")
  @Length(min = 2, message = "이름은 2글자 이상이어야 합니다.")
  private String name;

  @NotBlank(message = "이메일은 필수값입니다.")
  @Email(message = "이메일 형식이 아닙니다.")
  private String email;

  @NotNull(message = "부서는 필수값입니다.")
  private Integer departmentId;

  @NotBlank(message = "직함은 필수값입니다.")
  @Length(min = 2, message = "직함은 2글자 이상이어야 합니다.")
  private String position;

  @NotBlank(message = "입사일은 필수값입니다.")
  @Pattern(
      regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
      message = "\"0000-00-00\" 형식에 맞지 않습니다."
  )
  private String hireDate;

  // 이록 기록용
  private String memo;
}
