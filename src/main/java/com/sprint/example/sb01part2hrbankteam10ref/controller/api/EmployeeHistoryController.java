package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.DiffDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeHistoryCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.EmployeeHistoryResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeHistoryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class EmployeeHistoryController {

    private final EmployeeHistoryService employeeHistoryService;

    // 통합한 후 삭제 예정입니다..!
    @PostMapping
    public ResponseEntity<ChangeLogDto> create(@RequestBody EmployeeHistoryCreateRequest request) {
        ChangeLogDto savedHistory = employeeHistoryService.create(
                request.getEmployeeNumber(),
                request.getType(),
                request.getMemo(),
                request.getBeforeData(),
                request.getAfterData(),
                "127.0.0.임시값"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHistory);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseDto<EmployeeHistoryResponseDto>> getEmployeeHistories(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) ChangeType type,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime atFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime atTo,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortField") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {


        return ResponseEntity.ok()
            .body(employeeHistoryService.getEmployeeHistoriesWithCursor(
                employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, cursor, size, sortField, sortDirection
            ));
    }

    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<DiffDto>> getChangeDiffs(@PathVariable Integer id) {
        List<DiffDto> diffList = employeeHistoryService.getChangeDiffs(id);
        return ResponseEntity.ok(diffList);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countEmployeeHistories(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {

        LocalDateTime defaultFromDate = (fromDate != null) ? fromDate : LocalDateTime.now().minusDays(7);
        LocalDateTime defaultToDate = (toDate != null) ? toDate : LocalDateTime.now();

        Long count = employeeHistoryService.countEmployeeHistories(defaultFromDate, defaultToDate);

        return ResponseEntity.ok(count);
    }
}
