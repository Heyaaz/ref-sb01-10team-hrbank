package com.sprint.example.sb01part2hrbankteam10ref.repository.custom;

import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeHistoryRepositoryCustom {

  List<EmployeeHistory> findEmployeeHistoryWithCursor(
      String employeeNumber, ChangeType type, String memo, String ipAddress, LocalDateTime loggedAtFrom, LocalDateTime loggedAtTo,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection
  );
  
  Long countEmployeeHistories(
      String employeeNumber, ChangeType type, String memo, String ipAddress, LocalDateTime loggedAtFrom, LocalDateTime loggedAtTo
  );
}
