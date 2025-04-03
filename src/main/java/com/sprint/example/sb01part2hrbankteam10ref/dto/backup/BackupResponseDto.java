package com.sprint.example.sb01part2hrbankteam10ref.dto.backup;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class BackupResponseDto {
    private Integer id;
    private String worker;
    private BackupStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer fileId;

}
