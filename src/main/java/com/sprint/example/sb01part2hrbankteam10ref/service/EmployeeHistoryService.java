package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.DiffDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeHistoryService {

    ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
        String memo, EmployeeDto beforeDate, EmployeeDto afterData, String clientIp);

    CursorPageResponseChangeLogDto getEmployeeHistoriesByCursor(
            String employeeNumber,
            String type,
            String memo,
            String ipAddress,
            LocalDateTime atFrom,
            LocalDateTime atTo,
            Integer idAfter,
            int size,
            String sortField,
            String sortDirection
    );

    List<DiffDto> getChangeDiffs(Integer id);

    Long countEmployeeHistories(LocalDateTime fromDate, LocalDateTime toDate);

}
