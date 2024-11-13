package com.e205.payload;

import java.time.LocalDateTime;

public record CommentPayload(
    Integer id,
    Integer itemId,
    Integer writerId,
    Integer parentId,
    String content,
    LocalDateTime createdAt
) {

}
