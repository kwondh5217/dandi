package com.e205.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public sealed class NotificationResponse permits FoundItemNotificationResponse,
    LostItemNotificationResponse, RouteNotificationResponse, CommentNotificationResponse {

  private Integer id;
  private Integer memberId;
  private LocalDateTime createdAt;
  private char confirmation;
  private String title;


  public NotificationResponse(Integer id, Integer memberId, LocalDateTime createdAt,
      char confirmation, String title) {
    this.id = id;
    this.memberId = memberId;
    this.createdAt = createdAt;
    this.confirmation = confirmation;
    this.title = title;
  }
}