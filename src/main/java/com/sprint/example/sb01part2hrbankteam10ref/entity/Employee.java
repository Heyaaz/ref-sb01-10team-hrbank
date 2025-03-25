package com.sprint.example.sb01part2hrbankteam10ref.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @Column(name = "employee_number", length = 255, nullable = false, updatable = false, unique = true)
  private String employeeNumber;

  @Column(name = "position", length = 255)
  private String position;

  @Column(name = "hire_date", columnDefinition = "timestamp with time zone", nullable = false)
  private LocalDateTime hireDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EmployeeStatus status;

  @CreatedDate
  @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Getter
  @LastModifiedDate
  @Column(name = "updated_at",columnDefinition = "timestamp with time zone")
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", columnDefinition = "INTEGER")
  @OnDelete(action = OnDeleteAction.RESTRICT)
  private Department department;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JoinColumn(name = "profile_image_id")
  private BinaryContent profileImage;

  @Builder
  public Employee(String name, String email, String employeeNumber, String position,
      LocalDateTime hireDate, EmployeeStatus status, Department department, BinaryContent profileImage) {
    this.name = name;
    this.email = email;
    this.employeeNumber = employeeNumber;
    this.position = position;
    this.hireDate = hireDate;
    this.status = status;
    this.department = department;
    this.profileImage = profileImage;
  }


  public enum EmployeeStatus {
    ACTIVE,
    RESIGNED,
    ON_LEAVE
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updatePosition(String position) {
    this.position = position;
  }

  public void updateHireDate(LocalDateTime hireDate) {
    this.hireDate = hireDate;
  }

  public void updateStatus(EmployeeStatus status) {
    this.status = status;
  }

  public void updateDepartment(Department department) {
    this.department = department;
  }

  public void updateProfileImage(BinaryContent profileImage) {
    this.profileImage = profileImage;
  }
}
