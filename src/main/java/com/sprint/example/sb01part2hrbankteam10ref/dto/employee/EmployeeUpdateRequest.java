package com.sprint.example.sb01part2hrbankteam10ref.dto.employee;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@AllArgsConstructor
public class EmployeeUpdateRequest {

  @Length(min = 2, message = "이름은 2글자 이상이어야 합니다.")
  private String name;

  @Email(message = "이메일 형식이 아닙니다.")
  private String email;

  private Integer departmentId;

  @Length(min = 2, message = "직함은 2글자 이상이어야 합니다.")
  private String position;

  @Pattern(
      regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
      message = "\"0000-00-00\" 형식에 맞지 않습니다."
  )
  private String hireDate;

  private EmployeeStatus status;

  private String memo;
}
