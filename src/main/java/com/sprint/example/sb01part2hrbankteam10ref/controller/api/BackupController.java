package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.CursorPageResponseBackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.BackupMapper;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10ref.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/backups")
@RequiredArgsConstructor
public class BackupController{

    private final BackupService backupService;
    private final BackupRepository backupRepository;

    // 백업 요청
    @PostMapping
    public ResponseEntity<Integer> backup(HttpServletRequest request) {
        Integer backupId = backupService.performBackupByHand(request);
        return ResponseEntity.status(HttpStatus.OK).body(backupId);
    }

    // 백업 상태로 가장 최근 백업 얻기
    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLastBackup(
        @RequestParam(value = "status", defaultValue = "COMPLETED") Backup.BackupStatus status
    ){
        Backup lastBackup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status);

        // 백업이 없는 경우 빈 DTO 반환
        if (lastBackup == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new BackupDto());
        }

        BackupDto lastBackupDto = BackupMapper.toDto(lastBackup);
        return ResponseEntity.status(HttpStatus.OK).body(lastBackupDto);
    }

    // 백업 목록 조회
    @GetMapping
    public ResponseEntity<CursorPageResponseDto> getBackupList(
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) BackupStatus status,
            @RequestParam(required = false) LocalDateTime startedAtFrom,
            @RequestParam(required = false) LocalDateTime startedAtTo,
            @RequestParam(value = "fileId", required = false) Integer binaryContentId,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {


        return ResponseEntity.ok()
            .body(backupService.getBackupList(worker, status, startedAtFrom, startedAtTo,
               binaryContentId, idAfter, cursor, size, sortField, sortDirection));
    }
}


