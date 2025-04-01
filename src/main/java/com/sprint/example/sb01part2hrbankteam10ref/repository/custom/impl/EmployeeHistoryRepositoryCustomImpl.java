package com.sprint.example.sb01part2hrbankteam10ref.repository.custom.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import com.sprint.example.sb01part2hrbankteam10ref.entity.QEmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.EmployeeHistoryRepositoryCustom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Repository
public class EmployeeHistoryRepositoryCustomImpl implements EmployeeHistoryRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<EmployeeHistory> findEmployeeHistoryWithCursor(String employeeNumber, ChangeType type,
      String memo, String ipAddress, LocalDateTime loggedAtFrom, LocalDateTime loggedAtTo, Integer idAfter,
      String cursor, int size, String sortField, String sortDirection) {

    QEmployeeHistory employeeHistory = QEmployeeHistory.employeeHistory;

    boolean isAscending = "asc".equalsIgnoreCase(sortDirection);

    Integer cursorId = null;
    if (idAfter != null) {
      cursorId = idAfter;
    } else if (StringUtils.hasText(cursor)) {
      try {
        cursorId = Integer.parseInt(cursor);
      } catch (NumberFormatException e){
      }
    }

    // 기본 검색 조건
    BooleanExpression employeeNumberCondition = getEmployeeNumberCondition(employeeHistory, employeeNumber);
    BooleanExpression typeCondition = getTypeCondition(employeeHistory, type);
    BooleanExpression memoCondition = getMemoCondition(employeeHistory, memo);
    BooleanExpression ipAddressCondition = getIpAddressCondition(employeeHistory, ipAddress);
    BooleanExpression loggedAtCondition = getLoggedAtCondition(employeeHistory, loggedAtFrom, loggedAtTo);
    BooleanExpression cursorCondition = getCursorCondition(employeeHistory, cursorId, isAscending);

    // 조건들 결합
    BooleanExpression whereCondition = combineConditions(employeeNumberCondition, typeCondition,
        memoCondition, ipAddressCondition, loggedAtCondition, cursorCondition);


    // 쿼리 생성 및 실행
    return queryFactory
        .selectFrom(employeeHistory)
        .where(whereCondition)
        .orderBy(getOrderSpecifier(employeeHistory, sortField, isAscending))
        .limit(size)
        .fetch();
  }

  @Override
  public Long countEmployeeHistories(String employeeNumber, ChangeType type, String memo, 
                                    String ipAddress, LocalDateTime loggedAtFrom, LocalDateTime loggedAtTo) {
    QEmployeeHistory employeeHistory = QEmployeeHistory.employeeHistory;

    // 기본 검색 조건
    BooleanExpression employeeNumberCondition = getEmployeeNumberCondition(employeeHistory, employeeNumber);
    BooleanExpression typeCondition = getTypeCondition(employeeHistory, type);
    BooleanExpression memoCondition = getMemoCondition(employeeHistory, memo);
    BooleanExpression ipAddressCondition = getIpAddressCondition(employeeHistory, ipAddress);
    BooleanExpression loggedAtCondition = getLoggedAtCondition(employeeHistory, loggedAtFrom, loggedAtTo);

    // 조건들 결합
    BooleanExpression whereCondition = combineConditions(employeeNumberCondition, typeCondition,
        memoCondition, ipAddressCondition, loggedAtCondition);

    // 카운트 쿼리 실행
    return queryFactory
        .select(employeeHistory.count())
        .from(employeeHistory)
        .where(whereCondition)
        .fetchOne();
  }

  private BooleanExpression getEmployeeNumberCondition(QEmployeeHistory employeeHistory,
      String employeeNumber) {

    if(!StringUtils.hasText(employeeNumber)) {
      return null;
    }
    String searchPattern = "%" + employeeNumber.toLowerCase() + "%"; // 부분 일치 검색
    return employeeHistory.employeeNumber.toLowerCase().like(searchPattern);
  }

  private BooleanExpression getTypeCondition(QEmployeeHistory employeeHistory, ChangeType type) {
    if(type == null) {
      return null;
    }
  return employeeHistory.type.eq(type);
  }

  private BooleanExpression getMemoCondition(QEmployeeHistory employeeHistory, String memo) {
    if(!StringUtils.hasText(memo)){
      return null;
    }
    String searchPattern = "%" + memo.toLowerCase() + "%";
    return employeeHistory.memo.toLowerCase().like(searchPattern);
  }

  private BooleanExpression getIpAddressCondition(QEmployeeHistory employeeHistory,
      String ipAddress) {
    if(!StringUtils.hasText(ipAddress)) {
      return null;
    }
    String searchPattern = "%" + ipAddress.toLowerCase() + "%";
    return employeeHistory.ipAddress.toLowerCase().like(searchPattern);
  }

  private BooleanExpression getLoggedAtCondition(QEmployeeHistory employeeHistory,
      LocalDateTime loggedAtFrom, LocalDateTime loggedAtTo) {
    if(loggedAtFrom != null && loggedAtTo != null) {
      return employeeHistory.loggedAt.between(loggedAtFrom, loggedAtTo);
    } else if (loggedAtFrom != null) {
      return employeeHistory.loggedAt.goe(loggedAtFrom);
    } else if (loggedAtTo != null) {
      return employeeHistory.loggedAt.loe(loggedAtTo);
    } else {
      return null;
    }
  }

  private BooleanExpression getCursorCondition(QEmployeeHistory employeeHistory, Integer cursorId,
      boolean isAscending) {
    if(cursorId == null) {
      return null;
    }

    return isAscending
        ? employeeHistory.id.gt(cursorId)
        : employeeHistory.id.lt(cursorId);
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

  private OrderSpecifier<?> getOrderSpecifier(QEmployeeHistory employeeHistory, String sortField,
      boolean isAscending) {
    switch (sortField) {
      case "ipAddress":
        return isAscending ? employeeHistory.ipAddress.asc() : employeeHistory.ipAddress.desc();
      case "loggedAt":
        return isAscending ? employeeHistory.loggedAt.asc() : employeeHistory.loggedAt.desc();
      default:
        return isAscending ? employeeHistory.id.asc() : employeeHistory.id.desc();
    }
  }


}



