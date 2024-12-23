package com.e205.base.item.query;

import com.e205.base.item.CommentType;
import lombok.Builder;

@Builder
public record CommentListQuery(
    Integer cursor,
    Integer limit,
    CommentType type,
    Integer writerId,
    Integer itemId,
    Integer parentId,
    boolean fetchAll
) {

  public CommentListQuery {
    if (limit == null) limit = 10;
  }
}
