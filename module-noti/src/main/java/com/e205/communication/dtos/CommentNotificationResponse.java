package com.e205.communication.dtos;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class CommentNotificationResponse extends NotificationResponse {

  private Integer commentId;

  @Builder
  public CommentNotificationResponse(Integer id, Integer memberId, LocalDateTime createdAt,
      char confirmation, String title, Integer foundItemId) {
    super(id, memberId, createdAt, confirmation, title);
    this.commentId = foundItemId;
  }
}