package com.e205.item.dto;

import com.e205.payload.CommentPayload;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentResponse(
    Integer id,
    Integer itemId,
    Integer writerId,
    String nickname,
    Integer parentId,
    String content,
    LocalDateTime createdAt
) {

  public static CommentResponse from(CommentPayload payload, String nickname) {
    return CommentResponse.builder()
        .id(payload.id())
        .itemId(payload.itemId())
        .writerId(payload.writerId())
        .nickname(nickname)
        .parentId(payload.parentId())
        .content(payload.content())
        .createdAt(payload.createdAt())
        .build();
  }
}
