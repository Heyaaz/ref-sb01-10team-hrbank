package com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHistoryResponseDto {

  private Integer id;
  private String employeeNumber;
  private ChangeType type;
  private String memo;
  private String ipAddress;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS", timezone = "Asia/Seoul")
  private LocalDateTime at;

}
