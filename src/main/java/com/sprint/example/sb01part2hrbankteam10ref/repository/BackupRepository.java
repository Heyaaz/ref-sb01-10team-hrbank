package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.BackupRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Integer>,
    BackupRepositoryCustom {

  // 특정 상태의 가장 최근 백업 가져오기
  Backup findFirstByStatusOrderByStartedAtDesc(@Param("status") Backup.BackupStatus status);

  // 가장 최근 완료된 백업 시간 조회
  @Query("SELECT MAX(b.endedAt) FROM Backup b WHERE b.status = 'COMPLETED'")
  LocalDateTime findLastCompletedBackupAt();

  @Query("SELECT b FROM Backup b WHERE b.status = 'COMPLETED' ORDER BY b.createdAt DESC")
  Optional<Backup> findLastCompletedBackup();

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.updatedAt <= :backupDate")
  int countEmployeesBackedUpAt(@Param("backupDate") LocalDateTime backupDate);
}