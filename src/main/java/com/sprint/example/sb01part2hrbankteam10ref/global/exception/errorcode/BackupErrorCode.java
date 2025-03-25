package com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode;

import com.sprint.example.sb01part2hrbankteam10ref.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BackupErrorCode implements ErrorCode {
  BACKUP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BACKUP_001", "백업 도중 오류가 발생했습니다."),
  BACKUP_FILE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BACKUP_002", "백업을 CSV 파일로 변환 중 오류가 발생했습니다.");


  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
