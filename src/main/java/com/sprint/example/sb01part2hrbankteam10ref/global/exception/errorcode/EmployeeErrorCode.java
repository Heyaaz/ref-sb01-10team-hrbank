package com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode;

import com.sprint.example.sb01part2hrbankteam10ref.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmployeeErrorCode implements ErrorCode {

  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMPLOYEE_001", "해당 직원이 존재하지 않습니다."),
  EMAIL_IS_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "EMPLOYEE_002", "이미 사용중인 이메일입니다."),
  INVALID_DATE(HttpStatus.BAD_REQUEST, "EMPLOYEE_003", "유효하지 않은 날짜입니다."),
  INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "EMPLOYEE_004", "유효하지 않은 정렬 기준입니다."),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "EMPLOYEE_005", "종료일은 시작일보다 빠를 수 없습니다.");
  // 기본 유효성 검사는 @Valid 에서 하고, 중복 데이터 검사 등은 도메인 별로 ErrorCode 를 만들어 관리합니다.

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
