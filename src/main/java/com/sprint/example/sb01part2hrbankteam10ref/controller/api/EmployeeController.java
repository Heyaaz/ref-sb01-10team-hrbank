package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.controller.docs.EmployeeDocs;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeSearchRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeService;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeStatService;
import jakarta.servlet.http.HttpServletRequest;
import com.sprint.example.sb01part2hrbankteam10ref.util.IpUtil;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController implements EmployeeDocs {

  private final EmployeeService employeeService;
  private final EmployeeStatService employeeStatusService;


  @PostMapping
  @Override
  public ResponseEntity<EmployeeDto> createEmployee (
      HttpServletRequest httpServletRequest,
      @Valid @RequestPart(name = "employee")EmployeeCreateRequest request,
      @RequestPart(name = "profile", required = false) MultipartFile profile
  ) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(employeeService.create(request, profile, IpUtil.getClientIp(httpServletRequest)));
  }

  @PatchMapping("/{id}")
  @Override
  public ResponseEntity<EmployeeDto> updateEmployee (
      HttpServletRequest httpServletRequest,
      @PathVariable Integer id,
      @Valid @RequestPart(name = "employee") EmployeeUpdateRequest request,
      @RequestPart(name = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity.ok()
        .body(
            employeeService.update(id, request, profile, IpUtil.getClientIp(httpServletRequest))
        );
  }
  
  @GetMapping("/{id}")
  @Override
  public ResponseEntity<EmployeeDto> getEmployee (@PathVariable Integer id) {
    return ResponseEntity.ok()
        .body(employeeService.getById(id));
  }

  @DeleteMapping("/{id}")
  @Override
  public ResponseEntity<String> deleteEmployee (
      HttpServletRequest httpServletRequest,
      @PathVariable Integer id
  ) {
    return ResponseEntity.ok()
        .body(employeeService.deleteById(id, IpUtil.getClientIp(httpServletRequest)));
  }

  @GetMapping
  @Override
  public ResponseEntity<CursorPageResponseDto<EmployeeDto>> getListEmployee(
      @RequestParam(name = "nameOrEmail", required = false) String nameOrEmail,
      @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
      @RequestParam(name = "departmentName", required = false) String departmentName,
      @RequestParam(name = "position", required = false) String position,
      @RequestParam(name = "hireDateFrom", required = false) LocalDate hireDateFrom,
      @RequestParam(name = "hireDateTo", required = false) LocalDate hireDateTo,
      @RequestParam(name = "status", required = false) EmployeeStatus status,
      @RequestParam(name = "idAfter", required = false) Integer idAfter,
      @RequestParam(name = "cursor", required = false) String cursor,
      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
      @RequestParam(name = "sortField", required = false, defaultValue = "name") String sortField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "asc") String sortDirection
  ) {
    EmployeeSearchRequest request = new EmployeeSearchRequest(
        nameOrEmail, employeeNumber, departmentName, position,
        hireDateFrom, hireDateTo, status, idAfter, cursor, size, sortField, sortDirection
    );

    return ResponseEntity.ok()
        .body(employeeService.getAllByQuery(request));
  }

  @GetMapping("/stats/distribution")
  @Override
  public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
      @RequestParam(defaultValue = "department") String groupBy,
      @RequestParam(defaultValue = "ACTIVE") EmployeeStatus Status) {

    return ResponseEntity.ok()
        .body(employeeStatusService.getDistribution(groupBy, Status));
  }

  @GetMapping(value = "/stats/trend")
  @Override
  public ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to,
      @RequestParam(defaultValue = "month") String unit) {

    return ResponseEntity.ok()
        .body(employeeStatusService.getTrend(from, to, unit));
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getEmployeeCount(
      @RequestParam(required = false) EmployeeStatus status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

    return ResponseEntity.ok()
        .body(employeeStatusService.getCount(status, fromDate, toDate));
  }
}
