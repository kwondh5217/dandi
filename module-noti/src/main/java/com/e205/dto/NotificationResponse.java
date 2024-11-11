package com.e205.dto;

import com.e205.entity.Notification;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public sealed class NotificationResponse permits FoundItemNotificationResponse,
    LostItemNotificationResponse, RouteNotificationResponse, CommentNotificationResponse {

  private Integer id;
  private Integer memberId;
  private LocalDateTime createdAt;
  private boolean confirmation;
  private String title;
  private String body;


  public NotificationResponse(Integer id, Integer memberId, LocalDateTime createdAt,
      boolean confirmation, String title, String body) {
    this.id = id;
    this.memberId = memberId;
    this.createdAt = createdAt;
    this.confirmation = confirmation;
    this.title = title;
    this.body = body;
  }

  public static NotificationResponse fromEntity(Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getMemberId(),
        notification.getCreatedAt(),
        notification.isConfirmed(),
        notification.getTitle(),
        notification.getBody()
    );
  }
}