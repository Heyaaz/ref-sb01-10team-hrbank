package com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history;

import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChangeLogDto {

    private Integer id;
    private String employeeNumber;
    private String type;
    private String memo;
    private String ipAddress;
    private LocalDateTime at;

    // EmployeeHistory 객체를 받아서 ChangeLogDto로 변환
    public static ChangeLogDto fromEntity(EmployeeHistory history) {
        return new ChangeLogDto(
                history.getId(),
                history.getEmployeeNumber(),
                history.getType().name(),
                history.getMemo(),
                history.getIpAddress(),
                history.getLoggedAt()
        );
    }
}
