package com.sprint.example.sb01part2hrbankteam10ref.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  HttpStatus getHttpStatus();

  String getCode();

  String getMessage();
}