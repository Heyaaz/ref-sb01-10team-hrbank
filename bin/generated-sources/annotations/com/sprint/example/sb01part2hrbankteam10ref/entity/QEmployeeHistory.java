package com.sprint.example.sb01part2hrbankteam10ref.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmployeeHistory is a Querydsl query type for EmployeeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployeeHistory extends EntityPathBase<EmployeeHistory> {

    private static final long serialVersionUID = 1146111132L;

    public static final QEmployeeHistory employeeHistory = new QEmployeeHistory("employeeHistory");

    public final MapPath<String, Object, SimplePath<Object>> changedFields = this.<String, Object, SimplePath<Object>>createMap("changedFields", String.class, Object.class, SimplePath.class);

    public final StringPath employeeNumber = createString("employeeNumber");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final DateTimePath<java.time.LocalDateTime> loggedAt = createDateTime("loggedAt", java.time.LocalDateTime.class);

    public final StringPath memo = createString("memo");

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final EnumPath<EmployeeHistory.ChangeType> type = createEnum("type", EmployeeHistory.ChangeType.class);

    public QEmployeeHistory(String variable) {
        super(EmployeeHistory.class, forVariable(variable));
    }

    public QEmployeeHistory(Path<? extends EmployeeHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmployeeHistory(PathMetadata metadata) {
        super(EmployeeHistory.class, metadata);
    }

}

