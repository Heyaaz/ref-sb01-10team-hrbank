package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.DiffDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.EmployeeHistoryResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;

import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeHistoryService {

    ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
        String memo, EmployeeDto beforeDate, EmployeeDto afterData, String clientIp);

    CursorPageResponseDto<EmployeeHistoryResponseDto> getEmployeeHistoriesWithCursor(
            String employeeNumber,
            ChangeType type,
            String memo,
            String ipAddress,
            LocalDateTime atFrom,
            LocalDateTime atTo,
            Integer idAfter,
            String cursor,
            int size,
            String sortField,
            String sortDirection
    );

    List<DiffDto> getChangeDiffs(Integer id);

    Long countEmployeeHistories(LocalDate fromDate, LocalDate toDate);
}
