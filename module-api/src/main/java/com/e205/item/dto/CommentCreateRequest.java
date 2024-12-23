package com.e205.item.dto;

import com.e205.base.item.CommentType;
import com.e205.base.item.command.CommentCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
    Integer parentId,
    @NotBlank(message = "공백일 수 없습니다.")
    @Size(min = 1, max = 255, message = "글자 수는 {min}~{max} 사이여야 합니다.")
    String content
) {

  public CommentCreateCommand toCommand(int writerId, int itemId, CommentType type) {
    String newContent = content.replaceAll("\\n{3}", "\n\n");
    return CommentCreateCommand.builder()
        .commentType(type)
        .writerId(writerId)
        .itemId(itemId)
        .parentId(parentId)
        .content(newContent)
        .build();
  }
}
