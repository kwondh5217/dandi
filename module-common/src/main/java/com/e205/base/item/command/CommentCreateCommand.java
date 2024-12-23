package com.e205.base.item.command;

import com.e205.base.item.CommentType;
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
