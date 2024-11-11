package com.e205.repository;

import com.e205.query.CommentListQuery;
import lombok.Builder;

@Builder
public record CommentQueryCondition(
    Integer cursor,
    Integer limit,
    Integer writerId,
    Integer itemId,
    Integer parentId,
    boolean fetchAll
) {

  public static CommentQueryCondition from(CommentListQuery query) {
    return CommentQueryCondition.builder()
        .cursor(query.cursor())
        .limit(query.limit())
        .writerId(query.writerId())
        .itemId(query.itemId())
        .parentId(query.parentId())
        .fetchAll(query.fetchAll())
        .build();
  }
}
