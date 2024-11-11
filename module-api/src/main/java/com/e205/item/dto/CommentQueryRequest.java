package com.e205.item.dto;

import com.e205.CommentType;
import com.e205.query.CommentListQuery;

public record CommentQueryRequest(
    Integer cursor,
    Integer writerId,
    Integer parentId,
    boolean fetchAll
) {

  public CommentListQuery toQuery(CommentType type, int itemId) {
    return CommentListQuery.builder()
        .type(type)
        .cursor(cursor)
        .writerId(writerId)
        .itemId(itemId)
        .fetchAll(fetchAll)
        .parentId(parentId)
        .build();
  }
}
