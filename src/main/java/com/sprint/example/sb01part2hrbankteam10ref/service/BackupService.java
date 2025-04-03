package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

public interface BackupService {
  Integer performBackupByHand(HttpServletRequest request);

  Integer performBackupByBatch();

  CursorPageResponseDto<BackupResponseDto> getBackupList(
      String worker,
      BackupStatus status,
      LocalDateTime startedAtFrom,
      LocalDateTime startedAtTo,
      Integer fileId,
      Integer idAfter,
      String cursor,
      int size,
      String sortField,
      String sortDirection);
}
