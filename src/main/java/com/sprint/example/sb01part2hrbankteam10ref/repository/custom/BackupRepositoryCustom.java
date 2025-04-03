package com.sprint.example.sb01part2hrbankteam10ref.repository.custom;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface BackupRepositoryCustom {
  List<Backup> findBackupsWithCursor(
      LocalDateTime startedAt, LocalDateTime endedAt, String workerIpAddress, BackupStatus status,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection
  );


}
