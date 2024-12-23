package com.e205.base.item.query;

import com.e205.base.item.CommentType;
import lombok.Builder;

@Builder
public record CommentQuery(
    Integer writerId,
    CommentType type,
    Integer itemId,
    Integer parentId,
    Integer commentId
) {

}
