package com.e205.command;

import com.e205.CommentType;
import lombok.Builder;

@Builder
public record CommentCreateCommand(
    int writerId,
    int itemId,
    CommentType commentType,
    Integer parentId,
    String content
) {

}
