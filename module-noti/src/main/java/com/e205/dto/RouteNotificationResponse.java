package com.e205.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class RouteNotificationResponse extends NotificationResponse {

  private Integer routeId;

  @Builder
  public RouteNotificationResponse(Integer id, Integer memberId, LocalDateTime createdAt,
      boolean confirmation, String title, Integer routeId) {
    super(id, memberId, createdAt, confirmation, title);
    this.routeId = routeId;
  }
}