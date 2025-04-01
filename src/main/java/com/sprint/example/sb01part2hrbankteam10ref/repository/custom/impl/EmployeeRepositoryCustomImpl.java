package com.sprint.example.sb01part2hrbankteam10ref.repository.custom.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10ref.entity.QEmployee;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.EmployeeRepositoryCustom;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {


  private final JPAQueryFactory queryFactory;

  @Override
  public List<Employee> findEmployeesWithCursor(
      String nameOrEmail, String employeeNumber, String departmentName,
      String position, EmployeeStatus status, LocalDate hireDateFrom, LocalDate hireDateTo,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection) {

    QEmployee employee = QEmployee.employee;

    boolean isAscending = "asc".equalsIgnoreCase(sortDirection);

    Integer cursorId = null;
    if (idAfter != null) {
      cursorId = idAfter;
    } else if (StringUtils.hasText(cursor)) {
      try {
        cursorId = Integer.parseInt(cursor);
      } catch (NumberFormatException e) {
      }
    }

    // 기본 검색 조건
    BooleanExpression nameOrEmailCondition = getNameOrEmailCondition(employee, nameOrEmail);
    BooleanExpression employeeNumberCondition = getEmployeeNumberCondition(employee, employeeNumber);
    BooleanExpression departmentCondition = getDepartmentCondition(employee, departmentName);
    BooleanExpression positionCondition = getPositionCondition(employee, position);
    BooleanExpression statusCondition = getStatusCondition(employee, status);
    BooleanExpression hireDateCondition = getHireDateCondition(employee, hireDateFrom, hireDateTo);
    BooleanExpression cursorCondition = getCursorCondition(employee, cursorId, isAscending);


    // 조건들 결합
    BooleanExpression whereCondition = combineConditions(nameOrEmailCondition, employeeNumberCondition
        ,departmentCondition, positionCondition, statusCondition, hireDateCondition, cursorCondition);

    // 쿼리 생성 및 실행
    return queryFactory
        .selectFrom(employee)
        .leftJoin(employee.department).fetchJoin()
        .where(whereCondition)
        .orderBy(getOrderSpecifier(employee, sortField, isAscending))
        .limit(size)
        .fetch();
  }

  private BooleanExpression getNameOrEmailCondition(QEmployee employee, String nameOrEmail) {
    if (!StringUtils.hasText(nameOrEmail)) {
      return null;
    }
    String searchPattern = "%" + nameOrEmail + "%";
    return employee.name.toLowerCase().like(searchPattern)
        .or(employee.email.toLowerCase().like(searchPattern));
  }

  private BooleanExpression getEmployeeNumberCondition(QEmployee employee, String employeeNumber) {
    if (!StringUtils.hasText(employeeNumber)) {
      return null;
    }
    return employee.employeeNumber.toLowerCase().like("%" + employeeNumber.toLowerCase() + "%");
  }

  private BooleanExpression getDepartmentCondition(QEmployee employee, String departmentName) {
    if (!StringUtils.hasText(departmentName)) {
      return null;
    }
    return employee.department.name.toLowerCase().like("%" + departmentName + "%");
  }

  private BooleanExpression getPositionCondition(QEmployee employee, String position) {
    if (!StringUtils.hasText(position)) {
      return null;
    }
    return employee.position.toLowerCase().like("%" + position + "%");
  }

  private BooleanExpression getStatusCondition(QEmployee employee, EmployeeStatus status) {
    if (status == null) {
      return null;
    }
    return employee.status.eq(status);
  }

  private BooleanExpression getHireDateCondition(QEmployee employee, LocalDate hireDateFrom,
      LocalDate hireDateTo) {
    if (hireDateFrom != null && hireDateTo != null) {
      return employee.hireDate.between(hireDateFrom.atStartOfDay(), hireDateTo.atTime(23, 59, 59));
    } else if (hireDateFrom != null) {
      return employee.hireDate.goe(hireDateFrom.atStartOfDay());
    } else if (hireDateTo != null) {
      return employee.hireDate.loe(hireDateTo.atTime(23, 59, 59));
    } else {
      return null;
    }
  }


  private BooleanExpression getCursorCondition(QEmployee employee, Integer cursorId, boolean isAscending) {
    if (cursorId == null) {
      return null;
    }

    return isAscending
        ? employee.id.gt(cursorId)
        : employee.id.lt(cursorId);
  }

  private BooleanExpression combineConditions(BooleanExpression... conditions) {
    BooleanExpression result = null;

    for (BooleanExpression condition : conditions) {
      if(condition == null) {
        continue;
      }

      if(result == null) {
        result = condition;
      } else {
        result = result.and(condition);
      }
    }
    return result;
  }

  private OrderSpecifier<?> getOrderSpecifier(
      QEmployee employee, String sortField, boolean isAscending) {
    switch (sortField) {
      case "name":
        return isAscending ? employee.name.asc() : employee.name.desc();
      case "employeeNumber":
        return isAscending ? employee.employeeNumber.asc() : employee.employeeNumber.desc();
      case "hireDate":
        return isAscending ? employee.hireDate.asc() : employee.hireDate.desc();
      default:
        return isAscending ? employee.id.asc() : employee.id.desc();
    }
  }

  @Override
  public Employee findEmployeeByIdWithDepartment(Integer id) {
    QEmployee employee = QEmployee.employee;

    return queryFactory
        .selectFrom(employee)
        .leftJoin(employee.department).fetchJoin()
        .where(employee.id.eq(id))
        .fetchOne();
  }
}
