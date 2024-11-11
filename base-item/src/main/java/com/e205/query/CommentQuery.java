package com.e205.query;

import com.e205.CommentType;
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
