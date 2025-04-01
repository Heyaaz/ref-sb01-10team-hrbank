package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.CursorPageResponseBackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
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
    public ResponseEntity<CursorPageResponseBackupDto> getBackupList(
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) Backup.BackupStatus status,
            @RequestParam(required = false) String startedAtFrom,
            @RequestParam(required = false) String startedAtTo,
            @RequestParam(required = false) Integer binaryContentId,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(sortDirection, sortField));
        LocalDateTime startFrom = startedAtFrom != null ?
                LocalDateTime.parse(startedAtFrom, DateTimeFormatter.ISO_DATE_TIME) : null;
        LocalDateTime startTo = startedAtTo != null ?
                LocalDateTime.parse(startedAtTo, DateTimeFormatter.ISO_DATE_TIME) : null;
        Page<BackupDto> backupPage = backupService.getBackupList(worker, status, startFrom, startTo, binaryContentId, idAfter, null, size, sortField, sortDirection);

        List<BackupDto> content = backupPage.getContent();
        String nextCursor = backupPage.hasNext() && !content.isEmpty() ?
                encodeCursor(content.get(content.size() - 1)) : null;
        Integer nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).getId();

        CursorPageResponseBackupDto response = CursorPageResponseBackupDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .size(backupPage.getSize())
                .totalElements((int) backupPage.getTotalElements())
                .hasNext(backupPage.hasNext())
                .build();

        return ResponseEntity.ok(response);
    }
    private String encodeCursor(BackupDto backupDto) {
        if (backupDto == null || backupDto.getId() == null) {
            throw new IllegalArgumentException("Invalid cursor data");
        }
        return Base64.getEncoder().encodeToString((backupDto.getId().toString()).getBytes());
    }
}


