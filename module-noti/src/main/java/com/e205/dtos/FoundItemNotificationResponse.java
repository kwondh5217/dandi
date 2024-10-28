package com.e205.dtos;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class FoundItemNotificationResponse extends NotificationResponse {

  private Integer foundItemId;

  @Builder
  public FoundItemNotificationResponse(Integer id, Integer memberId, LocalDateTime createdAt,
      char confirmation, String title, Integer foundItemId) {
    super(id, memberId, createdAt, confirmation, title);
    this.foundItemId = foundItemId;
  }
}