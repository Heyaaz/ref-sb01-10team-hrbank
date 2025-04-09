package com.sprint.example.sb01part2hrbankteam10ref.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBackup is a Querydsl query type for Backup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBackup extends EntityPathBase<Backup> {

    private static final long serialVersionUID = 1922999916L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBackup backup = new QBackup("backup");

    public final QBinaryContent binaryContent;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> endedAt = createDateTime("endedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt", java.time.LocalDateTime.class);

    public final EnumPath<Backup.BackupStatus> status = createEnum("status", Backup.BackupStatus.class);

    public final StringPath workerIpAddress = createString("workerIpAddress");

    public QBackup(String variable) {
        this(Backup.class, forVariable(variable), INITS);
    }

    public QBackup(Path<? extends Backup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBackup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBackup(PathMetadata metadata, PathInits inits) {
        this(Backup.class, metadata, inits);
    }

    public QBackup(Class<? extends Backup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.binaryContent = inits.isInitialized("binaryContent") ? new QBinaryContent(forProperty("binaryContent")) : null;
    }

}

