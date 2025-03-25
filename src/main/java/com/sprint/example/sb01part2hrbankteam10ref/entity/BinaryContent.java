package com.sprint.example.sb01part2hrbankteam10ref.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BinaryContent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", length = 255, nullable = false)
  private String name;

  @Column(name = "content_type", length = 100, nullable = false)
  private String contentType;

  @Column(name = "size", nullable = false)
  private BigInteger size;

  @CreatedDate
  @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public BinaryContent(String name, String contentType, BigInteger size) {
    this.name = name;
    this.contentType = contentType;
    this.size = size;
  }
}
