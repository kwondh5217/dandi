package com.e205.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class CommentNotificationResponse extends NotificationResponse {

  private Integer commentId;

  @Builder
  public CommentNotificationResponse(Integer id, Integer memberId,
      LocalDateTime createdAt,
      boolean confirmation, String title, Integer commentId) {
    super(id, memberId, createdAt, confirmation, title);
    this.commentId = commentId;
  }
}