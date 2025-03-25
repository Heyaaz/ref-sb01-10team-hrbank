package com.sprint.example.sb01part2hrbankteam10ref.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Backup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @CreatedDate
  @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JoinColumn(name = "binary_content_id")
  private BinaryContent binaryContent;

  @Column(name = "started_at", columnDefinition = "timestamp with time zone")
  private LocalDateTime startedAt;

  @Column(name = "ended_at", columnDefinition = "timestamp with time zone")
  private LocalDateTime endedAt;

  @Column(name = "worker_ip_address", length = 255, nullable = false)
  private String workerIpAddress;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BackupStatus status;

  @Builder
  public Backup(BinaryContent binaryContent, LocalDateTime startedAt, LocalDateTime endedAt, String workerIpAddress,
      BackupStatus status, LocalDateTime batchDoneAt) {
    this.binaryContent = binaryContent;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
    this.workerIpAddress = workerIpAddress;
    this.status = status;
  }

  public void updateStatus(BackupStatus status, LocalDateTime endedAt, BinaryContent binaryContent) {
    this.status = status;
    this.endedAt = endedAt;
    this.binaryContent = binaryContent;
  }

  public enum BackupStatus {
    IN_PROGRESS,
    COMPLETED,
    SKIPPED,
    FAILED
  }
}

