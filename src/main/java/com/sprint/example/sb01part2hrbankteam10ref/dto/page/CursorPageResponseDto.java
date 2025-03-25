package com.sprint.example.sb01part2hrbankteam10ref.dto.page;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponseDto<T> {

  private List<T> content;
  private String nextCursor;
  private Long nextIdAfter;
  private int size;
  private Long totalElements;
  private boolean hasNext;
}
