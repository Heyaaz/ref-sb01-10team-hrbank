//package com.sprint.example.sb01part2hrbankteam10ref.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
//import jakarta.transaction.Transactional;
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
//@Transactional // 테스트 후 자동으로 롤백되도록 설정
//class DepartmentRepositoryTest {
//
//  @Autowired
//  private DepartmentRepository departmentRepository;
//  @Autowired
//  private EmployeeRepository employeeRepository;
//
//  @Test
//  void testSaveDepartment() {
//    //given
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    departmentRepository.save(department);
//
//    // when (생성, 수정, 삭제 (직원 존재 시 삭제되면 안됨))
//    Department result = departmentRepository.findById(department.getId()).orElseThrow();
//
//    // then
//    assertEquals(department.getName(), result.getName());
//    assertEquals(department.getDescription(), result.getDescription());
//    assertEquals(department.getEstablishedDate(), result.getEstablishedDate());
//  }
//
//  @Test
//  void testUpdateDepartment() {
//    //given
//    Department department = Department.builder()
//        .name("hr2")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    departmentRepository.save(department);
//
//    Department updateDepartment = departmentRepository.findById(department.getId()).orElseThrow();
//
//
//    // when (생성, 수정, 삭제 (직원 존재 시 삭제되면 안됨))
//    updateDepartment.setName("nohr");
//    Department result = departmentRepository.save(updateDepartment);
//
//    // then
//    assertEquals("nohr", result.getName());
//    assertEquals(department.getDescription(), result.getDescription());
//    assertEquals(department.getEstablishedDate(), result.getEstablishedDate());
//  }
//
//  @Test
//  void testDeleteDepartment() {
//    //given
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveDepartment = departmentRepository.save(department);
//
//    Department devDepartment = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveNoEmplDepartment = departmentRepository.save(department);
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
//    employeeRepository.save(employee);
//
//    // when (생성, 수정, 삭제 (직원 존재 시 삭제되면 안됨))
//
////    departmentRepository.deleteById(saveNoEmplDepartment.getId());
//    try {
//      departmentRepository.deleteById(saveDepartment.getId());
//    } catch (Exception e) {
//      assertNotNull(departmentRepository.findById(saveDepartment.getId()).orElse(null));
//    }
//
//    // then
////    assertNull(departmentRepository.findById(saveNoEmplDepartment.getId()).orElse(null));
////    assertNotNull(departmentRepository.findById(saveDepartment.getId()).orElse(null));
//  }
//}