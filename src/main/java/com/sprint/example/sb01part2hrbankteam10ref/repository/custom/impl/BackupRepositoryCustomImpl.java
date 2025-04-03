package com.sprint.example.sb01part2hrbankteam10ref.repository.custom.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import com.sprint.example.sb01part2hrbankteam10ref.entity.QBackup;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.BackupRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class BackupRepositoryCustomImpl implements BackupRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Backup> findBackupsWithCursor(LocalDateTime startedAt, LocalDateTime endedAt,
      String workerIpAddress, BackupStatus status, Integer idAfter, String cursor, int size,
      String sortField, String sortDirection) {

    QBackup backup = QBackup.backup;

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

    // 검색어 조건 구성
    BooleanExpression workerIpAddressCondition = getWorkerIpAddressCondition(backup, workerIpAddress);
    BooleanExpression statusCondition = getStatusCondition(backup, status);
    BooleanExpression startedAtCondition = getStartedAtCondition(backup, startedAt);
    BooleanExpression endedAtAtCondition = getEndedAtAtCondition(backup, endedAt);
    BooleanExpression cursorCondition = getCursorCondition(backup, cursorId, isAscending);

    // 조건들 결합
    BooleanExpression whereCondition = combineConditions(workerIpAddressCondition, statusCondition,
        startedAtCondition, endedAtAtCondition, cursorCondition);

    // 쿼리 생성 및 실행
    return queryFactory
        .selectFrom(backup)
        .where(whereCondition)
        .orderBy(getOrderSpecifier(backup, sortField, isAscending))
        .limit(size)
        .fetch();
  }

  private BooleanExpression getWorkerIpAddressCondition(QBackup backup, String workerIpAddress) {
    if(!StringUtils.hasText(workerIpAddress)) {
      return null;
    }
    String searchPattern = "%" + workerIpAddress + "%";
    return backup.workerIpAddress.toLowerCase().like(searchPattern);
  }

  private BooleanExpression getStatusCondition(QBackup backup, BackupStatus status) {
    if(status == null) {
      return null;
    }
    return backup.status.eq(status);
  }

  private BooleanExpression getStartedAtCondition(QBackup backup, LocalDateTime startedAt) {
    if(startedAt != null) {
      return backup.startedAt.goe(startedAt);
    } else {
      return null;
    }
  }

  private BooleanExpression getEndedAtAtCondition(QBackup backup, LocalDateTime endedAt) {
    if(endedAt != null) {
      return backup.endedAt.loe(endedAt);
    } else {
      return null;
    }
  }


  private BooleanExpression getCursorCondition(QBackup backup, Integer cursorId, boolean isAscending) {
    if (cursorId == null){
      return null;
    }
    return isAscending
        ? backup.id.gt(cursorId)
        : backup.id.lt(cursorId);
  }

  private BooleanExpression combineConditions(BooleanExpression... conditions) {

    BooleanExpression result = null;

    for (BooleanExpression condition : conditions) {
      if(condition == null) {
        continue;
      }

      if(result == null) {
        result = condition;
    }  else {
        result = result.and(condition);
    }
    }
    return result;
  }

  private OrderSpecifier<?> getOrderSpecifier(QBackup backup, String sortField,
      boolean isAscending) {

    switch (sortField) {
      case "startedAt" :
        return isAscending ? backup.startedAt.asc() : backup.startedAt.desc();
      case "endedAt" :
        return isAscending ? backup.endedAt.asc() : backup.endedAt.desc();
      default:
        return isAscending ? backup.id.asc() : backup.id.desc();
    }
  }


}
