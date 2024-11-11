package com.e205.item.dto;

import com.e205.CommentType;
import com.e205.command.CommentCreateCommand;

public record CommentCreateRequest(
    Integer parentId,
    String content
) {

  public CommentCreateCommand toCommand(int writerId, int itemId, CommentType type) {
    return CommentCreateCommand.builder()
        .commentType(type)
        .writerId(writerId)
        .itemId(itemId)
        .parentId(parentId)
        .content(content)
        .build();
  }
}
