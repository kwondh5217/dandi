package com.e205.item.dto;

import com.e205.CommentType;
import com.e205.command.CommentCreateCommand;
import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
    Integer parentId,
    @NotBlank
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
