package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.EmployeeRepositoryCustom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>,
    JpaSpecificationExecutor<Employee>, EmployeeRepositoryCustom {

  boolean existsByEmail(String email);

  Optional<Employee> findTopByOrderByIdDesc();

  //직원수 조회 - 부서별
  Integer countByDepartmentId(Integer departmentId);

  //부서에 속한 직원이 있는지 확인
  boolean existsByDepartmentId(Integer id);


  /**
   * 부서, 직함 별 직원 수 조회
   * TOOD: 부서, 직원 퍼센테이지 수정
   */
  @Query(value =
      "SELECT "
          + "new com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto("
          + "e.position, "
          + "COUNT(e.id), "
          + "COUNT(e) * 100.0 / (SELECT COUNT(e2) FROM Employee e2 WHERE e2.status IN ('ACTIVE', 'ON_LEAVE'))"
          + ") "
          + "FROM Employee e "
          + "WHERE e.status IN ('ACTIVE', 'ON_LEAVE') "
          + "GROUP BY e.position"
  )
  List<EmployeeDistributionDto> findGroupByPosition(
      @Param("position") String position,
      @Param("status") EmployeeStatus status
  );

  @Query( value =
      "SELECT "
          + "new com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto("
          + "d.name, COUNT(e), COUNT(e) * 100.0 / "
          + "(SELECT COUNT(e2) FROM Employee e2 WHERE e2.status IN ('ACTIVE', 'ON_LEAVE'))) "
          + "FROM Employee e "
          + "LEFT JOIN Department d "
          + "ON e.department.id = d.id "
          + "WHERE e.status IN ('ACTIVE', 'ON_LEAVE')"
          + "GROUP BY d.name"
  )
  List<EmployeeDistributionDto> findGroupByDepartment(
      @Param("position") String position,
      @Param("status") EmployeeStatus status
  );

  /**
   * 직원 수
   */

  @Query(value = """
    SELECT 
        DATE_TRUNC(:unit, e.hire_date) AS date, 
        COUNT(e.id) AS count,
        COUNT(e.id) - LAG(COUNT(e.id), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hire_date)) AS change,
        ((COUNT(e.id) - LAG(COUNT(e.id), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hire_date))) * 100.0 / 
         NULLIF(LAG(COUNT(e.id), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hire_date)), 0)) AS change_rate
    FROM Employee e
    WHERE e.hire_date BETWEEN :startDate AND :endDate
    GROUP BY DATE_TRUNC(:unit, e.hire_date)
    ORDER BY DATE_TRUNC(:unit, e.hire_date) ASC
""", nativeQuery = true)
  List<EmployeeTrendDto> findDataByUnit(
      @Param("unit") String unit,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  /**
   * 직원 추이 조회
   */

  @Query(value =
      "WITH dates AS (" +
          "  SELECT generate_series(" +
          "    DATE_TRUNC(:unit, CAST(:from AS timestamp))," +
          "    DATE_TRUNC(:unit, CAST(:to AS timestamp))," +
          "    CAST(:intervalNumber || :unit AS interval)" +
          "  ) AS date" +
          ")," +
          "employee_counts AS (" +
          "  SELECT " +
          "    d.date AS grouped_date, " +
          "    COUNT(DISTINCT e.id) AS count " +
          "  FROM dates d " +
          "  LEFT JOIN employees e ON e.hire_date <= d.date AND " +
          "    e.status = 'ACTIVE' " +
          "  GROUP BY d.date" +
          "  ORDER BY d.date" +
          ")" +
          "SELECT " +
          "  TO_CHAR(grouped_date, 'YYYY-MM-DD') AS period, " +
          "  count " +
          "FROM employee_counts " +
          "ORDER BY grouped_date",
      nativeQuery = true)
  List<Object[]> findEmployeeTrend(
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("unit") String unit,
      @Param("intervalNumber") String intervalNumber
  );
}
