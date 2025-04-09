package com.sprint.example.sb01part2hrbankteam10ref.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBinaryContent is a Querydsl query type for BinaryContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBinaryContent extends EntityPathBase<BinaryContent> {

    private static final long serialVersionUID = 1950943566L;

    public static final QBinaryContent binaryContent = new QBinaryContent("binaryContent");

    public final StringPath contentType = createString("contentType");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigInteger> size = createNumber("size", java.math.BigInteger.class);

    public QBinaryContent(String variable) {
        super(BinaryContent.class, forVariable(variable));
    }

    public QBinaryContent(Path<? extends BinaryContent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBinaryContent(PathMetadata metadata) {
        super(BinaryContent.class, metadata);
    }

}

