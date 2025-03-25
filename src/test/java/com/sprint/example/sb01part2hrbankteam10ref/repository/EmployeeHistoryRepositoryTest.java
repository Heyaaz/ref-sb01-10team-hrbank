//package com.sprint.example.sb01part2hrbankteam10.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sprint.example.sb01part2hrbankteam10.entity.Department;
//import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
//import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
//import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
//import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory.ChangeType;
//import io.swagger.v3.core.util.Json;
//import jakarta.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.HashMap;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
//@Transactional // 테스트 후 자동으로 롤백되도록 설정
//class EmployeeHistoryRepositoryTest {
//
//  @Autowired
//  private EmployeeHistoryRepository employeeHistoryRepository;
//  @Autowired
//  private DepartmentRepository departmentRepository;
//  @Autowired
//  private EmployeeRepository employeeRepository;
//
//  @Test
//  void testSaveEmployeeHistory() {
//
//    // given
//    /*
//    this.employeeNumber = employeeNumber;
//    this.type = type;
//    this.memo = memo;
//    this.modifiedAt = modifiedAt;
//    this.ipAddress = ipAddress;
//    this.changedFields = changedFields;
//    this.changedBy = changedBy;
//     */
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveDepartment = departmentRepository.save(department);
//
//    Employee employee = Employee.builder()
//        .name("yerim")
//        .email("yerim@gmail.com")
//        .profileImage(null)
//        .employeeNumber("num-011")
//        .department(saveDepartment)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//
//    List<HashMap<String, String>> change = new ArrayList<>();
//    change.add(new HashMap<>());
//    change.get(0).put("changeField", "name");
//    change.get(0).put("changeValue", "hello");
//    change.get(0).put("oldValue", "yerim");
//    ObjectMapper mapper = new ObjectMapper();
//    String changeFields = null;
//    try {
//      changeFields = mapper.writeValueAsString(change);
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//
//    EmployeeHistory employeeHistory = EmployeeHistory.builder()
//        .employeeNumber(employee.getEmployeeNumber())
//        .type(ChangeType.CREATED)
//        .memo("memo")
//        .modifiedAt(LocalDateTime.now())
//        .ipAddress("127.0.0.1")
//        .changedFields(changeFields)
//        .build();
//    employeeHistoryRepository.save(employeeHistory);
//
//    // when
//    EmployeeHistory getEmployeeHistory = employeeHistoryRepository.findById(employeeHistory.getId()).orElseThrow();
//
//    // then
//    assertEquals(getEmployeeHistory.getEmployeeNumber(), getEmployeeHistory.getEmployeeNumber());
//    assertEquals(changeFields, getEmployeeHistory.getChangedFields());
//  }
//}