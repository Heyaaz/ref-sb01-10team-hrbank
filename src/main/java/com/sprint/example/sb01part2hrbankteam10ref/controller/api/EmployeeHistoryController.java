package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.controller.docs.EmployeeHistoryDocs;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.DiffDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeHistoryCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class EmployeeHistoryController implements EmployeeHistoryDocs {

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
    @Override
    public ResponseEntity<CursorPageResponseChangeLogDto<ChangeLogDto>> getEmployeeHistories(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime atFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime atTo,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "at") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        CursorPageResponseChangeLogDto<ChangeLogDto> response =
                employeeHistoryService.getEmployeeHistoriesByCursor(
                        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, size, sortField, sortDirection);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/diffs")
    @Override
    public ResponseEntity<List<DiffDto>> getChangeDiffs(@PathVariable Integer id) {
        List<DiffDto> diffList = employeeHistoryService.getChangeDiffs(id);
        return ResponseEntity.ok(diffList);
    }

    @GetMapping("/count")
    @Override
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
