package com.sprint.example.sb01part2hrbankteam10ref.dto.backup;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupDto {
  Integer id;
  String worker;
  LocalDateTime startedAt;
  LocalDateTime endedAt;
  BackupStatus status;
  Integer fileId;
}
