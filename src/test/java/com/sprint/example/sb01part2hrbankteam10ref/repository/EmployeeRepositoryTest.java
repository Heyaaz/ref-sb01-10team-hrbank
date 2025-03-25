package com.sprint.example.sb01part2hrbankteam10ref.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
@Transactional // 테스트 후 자동으로 롤백되도록 설정
public class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository employeeRepository;
  @Autowired
  private DepartmentRepository departmentRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Test
  void testSaveEmployeeWithDepartment() {

    //given
    Department department = Department.builder()
        .name("hr")
        .description("인사팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department saveDepartment = departmentRepository.save(department);

    Employee employee = Employee.builder()
        .name("yerim")
        .email("yerim@gmail.com")
        .profileImage(null)
        .employeeNumber("num-011")
        .department(saveDepartment)
        .hireDate(LocalDateTime.now())
        .status(EmployeeStatus.ACTIVE)
        .position("개발자")
        .build();
    employeeRepository.save(employee);

    // when
    Employee saveEmployee = employeeRepository.findById(employee.getId()).orElseThrow();

    // then
    assertEquals("yerim", saveEmployee.getName());
    assertEquals("yerim@gmail.com", saveEmployee.getEmail());
    assertEquals("num-011", saveEmployee.getEmployeeNumber());
    assertEquals("hr", saveEmployee.getDepartment().getName());
  }

  @Test
  void testSaveEmployeeWithProfile() {

    //given
    Department department = Department.builder()
        .name("hr")
        .description("인사팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department saveDepartment = departmentRepository.save(department);

    BinaryContent profile = BinaryContent.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    BinaryContent saveProfile = binaryContentRepository.save(profile);

    Employee employee = Employee.builder()
        .name("yerim")
        .email("yerim@gmail.com")
        .profileImage(saveProfile)
        .employeeNumber("num-011")
        .department(saveDepartment)
        .hireDate(LocalDateTime.now())
        .status(EmployeeStatus.ACTIVE)
        .position("개발자")
        .build();
    employeeRepository.save(employee);

    // when
    Employee saveEmployee = employeeRepository.findById(employee.getId()).orElseThrow();

    // then
    assertEquals("backup2", saveEmployee.getProfileImage().getName());
    assertEquals(saveProfile.getContentType(), saveEmployee.getProfileImage().getContentType());
    assertEquals(saveProfile.getSize(), saveEmployee.getProfileImage().getSize());
  }

  @Test
  void testUpdateEmployeeWithDepartment() { // 부서

    //given
    Department department = Department.builder()
        .name("hr")
        .description("인사팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department saveDepartment = departmentRepository.save(department);

    Department department2 = Department.builder()
        .name("dev")
        .description("개발팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department saveDepartment2 = departmentRepository.save(department2);

    Employee employee = Employee.builder() // 인사팀
        .name("yerim")
        .email("yerim@gmail.com")
        .profileImage(null)
        .employeeNumber("num-011")
        .department(saveDepartment)
        .hireDate(LocalDateTime.now())
        .status(EmployeeStatus.ACTIVE)
        .position("개발자")
        .build();
    Employee saveEmployee = employeeRepository.save(employee);

    // when
    Employee getEmployee = employeeRepository.findById(saveEmployee.getId()).orElseThrow();
//    getEmployee.setDepartment(saveDepartment2); // 개발팀
    Employee updateEmployee = employeeRepository.save(getEmployee);
    Department hrDepartment = departmentRepository.findById(department.getId()).orElse(null);

    // then
    assertEquals("yerim", updateEmployee.getName());
    assertEquals("yerim@gmail.com", updateEmployee.getEmail());
    assertEquals("num-011", updateEmployee.getEmployeeNumber());
    assertEquals("dev", updateEmployee.getDepartment().getName());
    assertEquals(saveDepartment, hrDepartment);
  }

  @Test
  void testUpdateEmployeeWithProfile() {

    //given
    Department department = Department.builder()
        .name("hr")
        .description("인사팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department saveDepartment = departmentRepository.save(department);

    BinaryContent profile = BinaryContent.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    BinaryContent saveProfile = binaryContentRepository.save(profile);

    Employee employee = Employee.builder()
        .name("yerim")
        .email("yerim@gmail.com")
        .profileImage(saveProfile)
        .employeeNumber("num-011")
        .department(saveDepartment)
        .hireDate(LocalDateTime.now())
        .status(EmployeeStatus.ACTIVE)
        .position("개발자")
        .build();
    Employee preEmployee = employeeRepository.save(employee);

    // when

    // 업데이트
    Employee saveEmployee = employeeRepository.findById(preEmployee.getId()).orElseThrow();

    BinaryContent newProfile = BinaryContent.builder()
        .name("new")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
//    saveEmployee.setProfileImage(fileRepository.save(newProfile));

    Employee updateEmployee = employeeRepository.save(saveEmployee); // 새 프로필로 설정

    // 이전 프로필 삭제 확인
    binaryContentRepository.deleteById(profile.getId());
    BinaryContent deleteProfile = binaryContentRepository.findById(profile.getId()).orElse(null);

    // then
    assertEquals("new",updateEmployee.getProfileImage().getName());
    assertNull(deleteProfile);
  }

  @Test
  void testDeleteEmployeeWithDepartment() {

    // given
    Department department2 = Department.builder()
        .name("dev")
        .description("개발팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department devDepartment = departmentRepository.save(department2);

    Employee employee = Employee.builder() // 인사팀
        .name("yerim")
        .email("yerim@gmail.com")
        .profileImage(null)
        .employeeNumber("num-011")
        .department(devDepartment)
        .hireDate(LocalDateTime.now())
        .status(EmployeeStatus.ACTIVE)
        .position("개발자")
        .build();
    Employee saveEmployee = employeeRepository.save(employee);

    // when
    employeeRepository.deleteById(saveEmployee.getId());
    Employee getEmployee = employeeRepository.findById(saveEmployee.getId()).orElse(null);
    Department getDepartment = departmentRepository.findById(devDepartment.getId()).orElse(null);

    // then
    assertNull(getEmployee);
    assertEquals(devDepartment, getDepartment);
  }

  @Test
  void testDeleteEmployeeWithProfile() {

    // given
    Department department2 = Department.builder()
        .name("dev")
        .description("개발팀")
        .establishedDate(LocalDateTime.now())
        .build();
    Department devDepartment = departmentRepository.save(department2);

    BinaryContent profile = BinaryContent.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    BinaryContent saveProfile = binaryContentRepository.save(profile);

    Employee employee = Employee.builder() // 인사팀
        .name("yerim")
        .email("yerim@gmail.com")
        .profileImage(saveProfile)
        .employeeNumber("num-011")
        .department(devDepartment)
        .hireDate(LocalDateTime.now())
        .status(EmployeeStatus.ACTIVE)
        .position("개발자")
        .build();
    Employee saveEmployee = employeeRepository.save(employee);

    // when
    employeeRepository.deleteById(saveEmployee.getId());
    Employee getEmployee = employeeRepository.findById(saveEmployee.getId()).orElse(null);
    Department getDepartment = departmentRepository.findById(devDepartment.getId()).orElse(null);
    BinaryContent getProfile = binaryContentRepository.findById(saveProfile.getId()).orElse(null);

    // then
    assertNull(getEmployee);
    assertNull(getProfile);
    assertEquals(devDepartment, getDepartment);
  }
}
//package com.sprint.example.sb01part2hrbankteam10.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.sprint.example.sb01part2hrbankteam10.entity.Department;
//import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
//import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
//import com.sprint.example.sb01part2hrbankteam10.entity.File;
//import jakarta.transaction.Transactional;
//import java.math.BigInteger;
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
//@Transactional // 테스트 후 자동으로 롤백되도록 설정
//public class EmployeeRepositoryTest {
//
//  @Autowired
//  private EmployeeRepository employeeRepository;
//  @Autowired
//  private DepartmentRepository departmentRepository;
//  @Autowired
//  private FileRepository fileRepository;
//
//  @Test
//  void testSaveEmployeeWithDepartment() {
//
//    //given
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
//    employeeRepository.save(employee);
//
//    // when
//    Employee saveEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
//
//    // then
//    assertEquals("yerim", saveEmployee.getName());
//    assertEquals("yerim@gmail.com", saveEmployee.getEmail());
//    assertEquals("num-011", saveEmployee.getEmployeeNumber());
//    assertEquals("hr", saveEmployee.getDepartment().getName());
//  }
//
//  @Test
//  void testSaveEmployeeWithProfile() {
//
//    //given
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveDepartment = departmentRepository.save(department);
//
//    File profile = File.builder()
//        .name("backup1")
//        .contentType("application/zip")
//        .size(new BigInteger("1024"))
//        .build();
//    File saveProfile = fileRepository.save(profile);
//
//    Employee employee = Employee.builder()
//        .name("yerim")
//        .email("yerim@gmail.com")
//        .profileImage(saveProfile)
//        .employeeNumber("num-011")
//        .department(saveDepartment)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    employeeRepository.save(employee);
//
//    // when
//    Employee saveEmployee = employeeRepository.findById(employee.getId()).orElseThrow();
//
//    // then
//    assertEquals("backup2", saveEmployee.getProfileImage().getName());
//    assertEquals(saveProfile.getContentType(), saveEmployee.getProfileImage().getContentType());
//    assertEquals(saveProfile.getSize(), saveEmployee.getProfileImage().getSize());
//  }
//
//  @Test
//  void testUpdateEmployeeWithDepartment() { // 부서
//
//    //given
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveDepartment = departmentRepository.save(department);
//
//    Department department2 = Department.builder()
//        .name("dev")
//        .description("개발팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveDepartment2 = departmentRepository.save(department2);
//
//    Employee employee = Employee.builder() // 인사팀
//        .name("yerim")
//        .email("yerim@gmail.com")
//        .profileImage(null)
//        .employeeNumber("num-011")
//        .department(saveDepartment)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    Employee saveEmployee = employeeRepository.save(employee);
//
//    // when
//    Employee getEmployee = employeeRepository.findById(saveEmployee.getId()).orElseThrow();
//    getEmployee.setDepartment(saveDepartment2); // 개발팀
//    Employee updateEmployee = employeeRepository.save(getEmployee);
//    Department hrDepartment = departmentRepository.findById(department.getId()).orElse(null);
//
//    // then
//    assertEquals("yerim", updateEmployee.getName());
//    assertEquals("yerim@gmail.com", updateEmployee.getEmail());
//    assertEquals("num-011", updateEmployee.getEmployeeNumber());
//    assertEquals("dev", updateEmployee.getDepartment().getName());
//    assertEquals(saveDepartment, hrDepartment);
//  }
//
//  @Test
//  void testUpdateEmployeeWithProfile() {
//
//    //given
//    Department department = Department.builder()
//        .name("hr")
//        .description("인사팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department saveDepartment = departmentRepository.save(department);
//
//    File profile = File.builder()
//        .name("backup1")
//        .contentType("application/zip")
//        .size(new BigInteger("1024"))
//        .build();
//    File saveProfile = fileRepository.save(profile);
//
//    Employee employee = Employee.builder()
//        .name("yerim")
//        .email("yerim@gmail.com")
//        .profileImage(saveProfile)
//        .employeeNumber("num-011")
//        .department(saveDepartment)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    Employee preEmployee = employeeRepository.save(employee);
//
//    // when
//
//    // 업데이트
//    Employee saveEmployee = employeeRepository.findById(preEmployee.getId()).orElseThrow();
//
//    File newProfile = File.builder()
//        .name("new")
//        .contentType("application/zip")
//        .size(new BigInteger("1024"))
//        .build();
//    saveEmployee.setProfileImage(fileRepository.save(newProfile));
//
//    Employee updateEmployee = employeeRepository.save(saveEmployee); // 새 프로필로 설정
//
//    // 이전 프로필 삭제 확인
//    fileRepository.deleteById(profile.getId());
//    File deleteProfile = fileRepository.findById(profile.getId()).orElse(null);
//
//    // then
//    assertEquals("new",updateEmployee.getProfileImage().getName());
//    assertNull(deleteProfile);
//  }
//
//  @Test
//  void testDeleteEmployeeWithDepartment() {
//
//    // given
//    Department department2 = Department.builder()
//        .name("dev")
//        .description("개발팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department devDepartment = departmentRepository.save(department2);
//
//    Employee employee = Employee.builder() // 인사팀
//        .name("yerim")
//        .email("yerim@gmail.com")
//        .profileImage(null)
//        .employeeNumber("num-011")
//        .department(devDepartment)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    Employee saveEmployee = employeeRepository.save(employee);
//
//    // when
//    employeeRepository.deleteById(saveEmployee.getId());
//    Employee getEmployee = employeeRepository.findById(saveEmployee.getId()).orElse(null);
//    Department getDepartment = departmentRepository.findById(devDepartment.getId()).orElse(null);
//
//    // then
//    assertNull(getEmployee);
//    assertEquals(devDepartment, getDepartment);
//  }
//
//  @Test
//  void testDeleteEmployeeWithProfile() {
//
//    // given
//    Department department2 = Department.builder()
//        .name("dev")
//        .description("개발팀")
//        .establishedDate(LocalDateTime.now())
//        .build();
//    Department devDepartment = departmentRepository.save(department2);
//
//    File profile = File.builder()
//        .name("backup1")
//        .contentType("application/zip")
//        .size(new BigInteger("1024"))
//        .build();
//    File saveProfile = fileRepository.save(profile);
//
//    Employee employee = Employee.builder() // 인사팀
//        .name("yerim")
//        .email("yerim@gmail.com")
//        .profileImage(saveProfile)
//        .employeeNumber("num-011")
//        .department(devDepartment)
//        .hireDate(LocalDateTime.now())
//        .status(EmployeeStatus.ACTIVE)
//        .position("개발자")
//        .build();
//    Employee saveEmployee = employeeRepository.save(employee);
//
//    // when
//    employeeRepository.deleteById(saveEmployee.getId());
//    Employee getEmployee = employeeRepository.findById(saveEmployee.getId()).orElse(null);
//    Department getDepartment = departmentRepository.findById(devDepartment.getId()).orElse(null);
//    File getProfile = fileRepository.findById(saveProfile.getId()).orElse(null);
//
//    // then
//    assertNull(getEmployee);
//    assertNull(getProfile);
//    assertEquals(devDepartment, getDepartment);
//  }
//}
