package com.e205.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class LostItemNotificationResponse extends NotificationResponse {

  private Integer lostItemId;

  @Builder
  public LostItemNotificationResponse(Integer id, Integer memberId,
      LocalDateTime createdAt,
      boolean confirmation, String title, Integer lostItemId, String body) {
    super(id, memberId, createdAt, confirmation, title, body);
    this.lostItemId = lostItemId;
  }
}