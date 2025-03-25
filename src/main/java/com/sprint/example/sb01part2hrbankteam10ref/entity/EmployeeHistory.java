package com.sprint.example.sb01part2hrbankteam10ref.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "employee_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class EmployeeHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "employee_number", nullable = false, length = 50)
  private String employeeNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ChangeType type;

  @Column(name = "memo", columnDefinition = "TEXT")
  private String memo;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Column(name = "ip_address", nullable = false, length = 50)
  private String ipAddress;

  @Column(name = "changed_fields", columnDefinition = "jsonb", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> changedFields;

  @CreatedDate
  @Column(name = "logged_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime loggedAt;

  @Builder
  public EmployeeHistory(String employeeNumber, ChangeType type, String memo,
      LocalDateTime modifiedAt, String ipAddress, Map<String, Object> changedFields) {
    this.employeeNumber = employeeNumber;
    this.type = type;
    this.memo = memo;
    this.modifiedAt = modifiedAt;
    this.ipAddress = ipAddress;
    this.changedFields = changedFields;
  }

  public enum ChangeType {
    CREATED, UPDATED, DELETED
  }
}
