package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.specification.EmployeeSpecification;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeStatService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeStatServiceImpl implements EmployeeStatService {

  private final EmployeeRepository employeeRepository;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
  private final BackupRepository backupRepository;

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> getDistribution(String groupBy, EmployeeStatus status) {

    if (groupBy.equals("position")) {
      return employeeRepository.findGroupByPosition(groupBy, status);

    } else {
      return employeeRepository.findGroupByDepartment(groupBy, status);
    }

  }

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> getTrend(LocalDateTime from, LocalDateTime to, String unit) {
    // 기본적으로 최근 12개의 단위를 표시
    if (from == null || to == null) {
      to = LocalDateTime.now();
    }

    if (!List.of("day", "week", "month", "quarter", "year").contains(unit)) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_STATUS_NOT_VALID, unit);
    }

    String intervalNumber = "1 ";

    // 시간 단위에 따라 from 값을 조정하여 정확히 12개의 데이터 포인트가 생성되도록 함
    switch (unit) {
      case "day":
        from = to.minusDays(11); // 12일 (현재 포함)
        break;
      case "week":
        from = to.minusWeeks(11); // 12주 (현재 포함)
        break;
      case "month":
        from = to.minusMonths(11); // 12개월 (현재 포함)
        break;
      case "quarter":
        from = to.minusMonths(33); // 12분기 (현재 포함) = 약 3년
        unit = "month";
        intervalNumber = "3 ";
        break;
      case "year":
        from = to.minusYears(11); // 12년 (현재 포함)
        break;
      default:
        from = to.minusMonths(11); // 기본값은 12개월
        break;
    }

    List<Object[]> trends = employeeRepository.findEmployeeTrend(from, to, unit, intervalNumber);

    // Object[]를 EmployeeTrendDto로 변환 및 change, changeRate 계산
    List<EmployeeTrendDto> result = new ArrayList<>();
    for (int i = 0; i < trends.size(); i++) {
      Object[] row = trends.get(i);
      String dateStr = (String) row[0]; // period
      Long count = ((Number) row[1]).longValue(); // count as Long
      Long change = (i > 0) ? (count - ((Number) trends.get(i - 1)[1]).longValue()) : 0L;
      Double changeRate = (i > 0 && ((Number) trends.get(i - 1)[1]).longValue() != 0)
          ? round(((double) (count - ((Number) trends.get(i - 1)[1]).longValue()) * 100)
          / ((Number) trends.get(i - 1)[1]).longValue(), 2)
          : 0.0;

      // Format the date based on the unit
      String formattedDate;
      switch (unit) {
        case "day":
          formattedDate = dateStr; // Keep YYYY-MM-DD
          break;
        case "week":
          LocalDate weekDate = LocalDate.parse(dateStr);
          formattedDate = weekDate.plusDays(weekDate.getDayOfWeek().getValue()-1).toString(); // Keep YYYY-MM-DD for the first day of the week
          break;
        case "month":
          // Take just year and month, and add the day as 20
//          formattedDate = dateStr.substring(0, 7) + "-20";
          formattedDate = dateStr.substring(0, 7) + "-" + to.getDayOfMonth();
          break;
        case "quarter":
          // For quarter, we'll determine which quarter it is from the date
          int month = Integer.parseInt(dateStr.substring(5, 7));
          int quarter = (month - 1) / 3 + 1;
          formattedDate = dateStr.substring(0, 4) + "-" +
              (quarter == 1 ? "03" : quarter == 2 ? "06" : quarter == 3 ? "09" : "12") +
              "-20";
          break;
        case "year":
          // For year, format as YYYY-03-20 -> 2014, 2015
          formattedDate = dateStr.substring(0, 4) + "-"+ to.getMonthValue() +"-" + to.getDayOfMonth();
          break;
        default:
          formattedDate = dateStr;
          break;
      }

      result.add(new EmployeeTrendDto(
          formattedDate,
          count,
          change,
          changeRate
      ));
    }

    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public Long getCount(EmployeeStatus status, LocalDate fromDate,
      LocalDate toDate) {

    LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
    LocalDateTime toDateTime = toDate != null ? toDate.atStartOfDay() : LocalDateTime.now();

    log.info("status: {}, fromDate: {}, toDate: {}", status, fromDateTime, toDateTime);
    Specification<Employee> specification = getCountSpecification(status, fromDateTime, toDateTime);

    return employeeRepository.count(specification);
  }


  private Specification<Employee> getCountSpecification(EmployeeStatus status, LocalDateTime fromDate,
      LocalDateTime toDate) {

    Specification<Employee> spec = ((root, query, criteriaBuilder) -> null);

    if (status != null) {
      spec = spec.and(EmployeeSpecification.equalStatus(status));
    }

    if (fromDate != null) {
      spec = spec.and(EmployeeSpecification.equalOrGreaterThanHireDateFrom(fromDate));
    }

    if (toDate != null) {
      spec = spec.and(EmployeeSpecification.lessThanHireDate(toDate));
    }

    return spec;
  }

  private Specification<Employee> getDistributionSpecification(String groupBy, EmployeeStatus status) {
    Specification<Employee> spec = ((root, query, criteriaBuilder) -> null);

    if (status != null) {
      spec = spec.and(EmployeeSpecification.equalStatus(status));
    }

    return spec;
  }

  private Double round(Double value, int places) {
    if (value == null) {
      return 0.0;
    }
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }
}
