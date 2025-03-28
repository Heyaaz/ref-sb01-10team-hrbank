package com.sprint.example.sb01part2hrbankteam10ref.repository.custom.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.entity.QDepartment;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.DepartmentRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Department> findDepartmentsWithCursor(
      String nameOrDescription,
      Integer cursorId,
      int size,
      String sortField,
      boolean isAscending) {

    QDepartment department = QDepartment.department;

    // 검색어 조건 구성
    BooleanExpression searchCondition = getSearchCondition(department, nameOrDescription);
    
    // 커서 조건 구성 
    BooleanExpression cursorCondition = getCursorCondition(department, cursorId, isAscending);
    
    // 조건들 결합
    BooleanExpression whereCondition = combineConditions(searchCondition, cursorCondition);

    // 쿼리 생성 및 실행
    return queryFactory
        .selectFrom(department)
        .where(whereCondition)
        .orderBy(getOrderSpecifier(department, sortField, isAscending))
        .limit(size)
        .fetch();
  }

  private BooleanExpression getSearchCondition(QDepartment department, String nameOrDescription) {
    if (!StringUtils.hasText(nameOrDescription)) {
      return null;
    }
    
    String searchPattern = "%" + nameOrDescription.toLowerCase() + "%"; // 부분 일치 검색  ex) apple 검색 -> apple, apple pie 등
    return department.name.toLowerCase().like(searchPattern)
        .or(department.description.toLowerCase().like(searchPattern));
  }

  private BooleanExpression getCursorCondition(QDepartment department, Integer cursorId, boolean isAscending) {
    if (cursorId == null) {
      return null;
    }
    
    return isAscending 
        ? department.id.gt(cursorId)
        : department.id.lt(cursorId);
  }

  private BooleanExpression combineConditions(BooleanExpression... conditions) {
    BooleanExpression result = null;
    
    for (BooleanExpression condition : conditions) {
      if (condition == null) {
        continue;
      }
      
      if (result == null) {
        result = condition;
      } else {
        result = result.and(condition);
      }
    }
    
    return result;
  }

  private OrderSpecifier<?> getOrderSpecifier(
      QDepartment department, String sortField, boolean isAscending) {
    
    switch (sortField) {
      case "name":
        return isAscending ? department.name.asc() : department.name.desc();
      case "description":
        return isAscending ? department.description.asc() : department.description.desc();
      case "establishedDate":
        return isAscending ? department.establishedDate.asc() : department.establishedDate.desc();
      default:
        // 기본값은 ID로 정렬
        return isAscending ? department.id.asc() : department.id.desc();
    }
  }
}
