package com.sprint.example.sb01part2hrbankteam10ref.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String details;
}
