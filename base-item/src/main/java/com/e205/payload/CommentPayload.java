package com.e205.payload;

import java.time.LocalDateTime;

public record CommentPayload(
    Integer id,
    Integer writerId,
    Integer parentId,
    String content,
    LocalDateTime createdAt
) {

}
