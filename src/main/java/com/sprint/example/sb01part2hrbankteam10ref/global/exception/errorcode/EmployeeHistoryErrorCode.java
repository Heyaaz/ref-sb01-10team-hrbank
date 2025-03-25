package com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode;

import com.sprint.example.sb01part2hrbankteam10ref.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmployeeHistoryErrorCode implements ErrorCode {

    EMPLOYEE_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "EMPLOYEE_HISTORY_001", "해당 직원 이력이 존재하지 않습니다."),
    INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "EMPLOYEE_HISTORY_002", "잘못된 요청입니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "EMPLOYEE_HISTORY_003", "유효하지 않은 날짜 범위입니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
