package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Integer>, JpaSpecificationExecutor<EmployeeHistory>{
  @Query("SELECT MAX(e.modifiedAt) FROM EmployeeHistory e")
  LocalDateTime findLastModifiedAt();

  Long countByModifiedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}