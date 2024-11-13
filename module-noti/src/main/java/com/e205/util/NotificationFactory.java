package com.e205.util;

import com.e205.dto.CommentNotificationResponse;
import com.e205.dto.FoundItemNotificationResponse;
import com.e205.dto.LostItemNotificationResponse;
import com.e205.dto.NotificationResponse;
import com.e205.dto.RouteNotificationResponse;
import com.e205.entity.CommentNotification;
import com.e205.entity.FoundItemNotification;
import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import com.e205.entity.RouteNotification;
import com.e205.service.NotificationType;

public class NotificationFactory {

  public static Notification createNotification(String type, Integer resourceId) {
    String notificationBody = createNotificationBody(type, resourceId);
    Notification notification;

    switch (NotificationType.fromString(type)) {
      case LOST_ITEM -> {
        notification = new LostItemNotification();
        ((LostItemNotification) notification).setLostItemId(resourceId);
      }
      case FOUND_ITEM -> {
        notification = new FoundItemNotification();
        ((FoundItemNotification) notification).setFoundItemId(resourceId);
      }
      case FOUND_COMMENT, LOST_COMMENT -> {
        notification = new CommentNotification();
        ((CommentNotification) notification).setCommentId(resourceId);
      }
      case ROUTE -> {
        notification = new RouteNotification();
        ((RouteNotification) notification).setRouteId(resourceId);
      }
      default -> throw new IllegalArgumentException("Unknown notification type: " + type);
    }
    notification.setBody(notificationBody);
    return notification;
  }

  public static String createNotificationBody(String type, Integer resourceId) {
    return "{\"resourceId\":\"" + resourceId + "\",\"eventType\":\"" + type + "\"}";
  }

  public static NotificationResponse convertToDto(Notification notification) {
    return switch (NotificationType.fromString(notification.getEntityType())) {
      case COMMENT, FOUND_COMMENT, LOST_COMMENT -> {
        CommentNotification commentNotification = (CommentNotification) notification;
        yield CommentNotificationResponse.builder()
            .id(commentNotification.getId())
            .memberId(commentNotification.getMemberId())
            .createdAt(commentNotification.getCreatedAt())
            .confirmation(commentNotification.isConfirmed())
            .title(commentNotification.getTitle())
            .commentId(commentNotification.getCommentId())
            .body(commentNotification.getBody())
            .build();
      }
      case LOST_ITEM -> {
        LostItemNotification lostItemNotification = (LostItemNotification) notification;
        yield LostItemNotificationResponse.builder()
            .id(lostItemNotification.getId())
            .memberId(lostItemNotification.getMemberId())
            .createdAt(lostItemNotification.getCreatedAt())
            .confirmation(lostItemNotification.isConfirmed())
            .title(lostItemNotification.getTitle())
            .lostItemId(lostItemNotification.getLostItemId())
            .body(lostItemNotification.getBody())
            .build();
      }
      case FOUND_ITEM -> {
        FoundItemNotification foundItemNotification = (FoundItemNotification) notification;
        yield FoundItemNotificationResponse.builder()
            .id(foundItemNotification.getId())
            .memberId(foundItemNotification.getMemberId())
            .createdAt(foundItemNotification.getCreatedAt())
            .confirmation(foundItemNotification.isConfirmed())
            .title(foundItemNotification.getTitle())
            .foundItemId(foundItemNotification.getFoundItemId())
            .body(foundItemNotification.getBody())
            .build();
      }
      case ROUTE -> {
        RouteNotification routeNotification = (RouteNotification) notification;
        yield RouteNotificationResponse.builder()
            .id(routeNotification.getId())
            .memberId(routeNotification.getMemberId())
            .createdAt(routeNotification.getCreatedAt())
            .confirmation(routeNotification.isConfirmed())
            .title(routeNotification.getTitle())
            .routeId(routeNotification.getRouteId())
            .body(routeNotification.getBody())
            .build();
      }
    };
  }
}
