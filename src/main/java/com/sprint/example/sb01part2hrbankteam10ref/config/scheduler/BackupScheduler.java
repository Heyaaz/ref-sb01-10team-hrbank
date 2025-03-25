package com.sprint.example.sb01part2hrbankteam10ref.config.scheduler;

import com.sprint.example.sb01part2hrbankteam10ref.service.BackupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    private final BackupService backupService;

    public BackupScheduler(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(fixedRate = 3600000) // 3,600,000 밀리초 = 1시간
    // 또는 cron 표현식 사용: @Scheduled(cron = "0 0 * * * *") // 매 시간 정각에 실행
    public void scheduledBackup() {
        backupService.performBackupByBatch();
    }
}