package com.sprint.example.sb01part2hrbankteam10ref.global.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"timestamp", "status", "message", "details"})
public class ErrorResponse {

  private final LocalDateTime timestamp;
  private final HttpStatus status;
  private final String message;
  private final String details;

  @Builder
  public ErrorResponse(LocalDateTime timestamp, HttpStatus status, String message, String details) {
    this.timestamp = timestamp;
    this.status = status;
    this.message = message;
    this.details = details;
  }
}
