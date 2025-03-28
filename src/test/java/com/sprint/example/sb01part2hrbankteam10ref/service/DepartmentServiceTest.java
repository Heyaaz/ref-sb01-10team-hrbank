//package com.sprint.example.sb01part2hrbankteam10ref.service;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
//import com.sprint.example.sb01part2hrbankteam10ref.repository.DepartmentRepository;
//import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeRepository;
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@Transactional
//class DepartmentServiceTest {
//
//
//  @Autowired
//  private DepartmentRepository departmentRepository;
//
//  @Autowired
//  private EmployeeRepository employeeRepository;
//
//  @Test
//  @DisplayName("직원 추가 후 부서 테스트")
//  void testEmployeeCountUpdate() {
//    //given
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department savedDept = departmentRepository.save(department);
//
//    //when
//    Employee employee1 = Employee.builder()
//        .name("employee1")
//        .email("emp1@gmail.com")
//        .department(savedDept)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    employeeRepository.save(employee1);
//
//    Employee employee2 = Employee.builder()
//        .name("employee2")
//        .email("emp2@gmail.com")
//        .department(savedDept)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    employeeRepository.save(employee2);
//
//    //then 부서 생성 테스트
//    assertThat(savedDept.getId()).isNotNull();  //부서 생성 확인
//  }
//}