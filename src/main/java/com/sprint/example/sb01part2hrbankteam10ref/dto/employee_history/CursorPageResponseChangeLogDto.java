package com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history;

import lombok.Data;

import java.util.List;

@Data
public class CursorPageResponseChangeLogDto<T> {

    private List<T> content;
    private String nextCursor;
    private Integer nextIdAfter;
    private Integer size;
    private Long totalElements;
    private boolean hasNext;

    public CursorPageResponseChangeLogDto(List<T> content, String nextCursor, Integer nextIdAfter, Integer size, Long totalElements, boolean hasNext) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.nextIdAfter = nextIdAfter;
        this.size = size;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
}
