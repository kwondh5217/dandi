package com.e205.dtos;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class LostItemNotificationResponse extends NotificationResponse {

  private Integer lostItemId;

  @Builder
  public LostItemNotificationResponse(Integer id, Integer memberId, LocalDateTime createdAt,
      char confirmation, String title, Integer lostItemId) {
    super(id, memberId, createdAt, confirmation, title);
    this.lostItemId = lostItemId;
  }
}