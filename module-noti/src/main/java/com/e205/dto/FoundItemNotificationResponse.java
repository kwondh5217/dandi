package com.e205.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class FoundItemNotificationResponse extends NotificationResponse {

  private Integer foundItemId;

  @Builder
  public FoundItemNotificationResponse(Integer id, Integer memberId,
      LocalDateTime createdAt,
      boolean confirmation, String title, Integer foundItemId, String body) {
    super(id, memberId, createdAt, confirmation, title, body);
    this.foundItemId = foundItemId;
  }
}