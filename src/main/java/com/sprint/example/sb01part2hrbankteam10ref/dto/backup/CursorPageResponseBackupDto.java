package com.sprint.example.sb01part2hrbankteam10ref.dto.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponseBackupDto {
    List<BackupDto> content;
    String nextCursor;
    Integer nextIdAfter;
    Integer size;
    Integer totalElements;
    boolean hasNext;
}
