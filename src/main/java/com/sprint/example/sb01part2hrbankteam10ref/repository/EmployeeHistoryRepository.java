package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.EmployeeHistoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Integer>,
    EmployeeHistoryRepositoryCustom {
  @Query("SELECT MAX(e.modifiedAt) FROM EmployeeHistory e")
  LocalDateTime findLastModifiedAt();

  Long countByLoggedAtBetween(LocalDate fromDate, LocalDate toDate);

  Long countByModifiedAtBetween(LocalDate modifiedAtAfter, LocalDate modifiedAtBefore);
}
