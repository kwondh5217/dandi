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

public class NotificationFactory {

  public static Notification createNotification(String type, Integer resourceId) {
    Notification notification;

    switch (type) {
      case "lostItem" -> {
        notification = new LostItemNotification();
        ((LostItemNotification) notification).setLostItemId(resourceId);
      }
      case "foundItem" -> {
        notification = new FoundItemNotification();
        ((FoundItemNotification) notification).setFoundItemId(resourceId);
      }
      case "comment" -> {
        notification = new CommentNotification();
        ((CommentNotification) notification).setCommentId(resourceId);
      }
      case "route" -> {
        notification = new RouteNotification();
        ((RouteNotification) notification).setRouteId(resourceId);
      }
      default -> throw new IllegalArgumentException("Unknown notification type: " + type);
    }

    return notification;
  }

  public static NotificationResponse convertToDto(Notification notification) {
    if (notification instanceof CommentNotification commentNotification) {
      return CommentNotificationResponse.builder()
          .id(commentNotification.getId())
          .memberId(commentNotification.getMemberId())
          .createdAt(commentNotification.getCreatedAt())
          .confirmation(commentNotification.isConfirmed())
          .title(commentNotification.getTitle())
          .commentId(commentNotification.getCommentId())
          .build();
    } else if (notification instanceof LostItemNotification lostItemNotification) {
      return LostItemNotificationResponse.builder()
          .id(lostItemNotification.getId())
          .memberId(lostItemNotification.getMemberId())
          .createdAt(lostItemNotification.getCreatedAt())
          .confirmation(lostItemNotification.isConfirmed())
          .title(lostItemNotification.getTitle())
          .lostItemId(lostItemNotification.getLostItemId())
          .build();
    } else if (notification instanceof FoundItemNotification foundItemNotification) {
      return FoundItemNotificationResponse.builder()
          .id(foundItemNotification.getId())
          .memberId(foundItemNotification.getMemberId())
          .createdAt(foundItemNotification.getCreatedAt())
          .confirmation(foundItemNotification.isConfirmed())
          .title(foundItemNotification.getTitle())
          .foundItemId(foundItemNotification.getFoundItemId())
          .build();
    } else if (notification instanceof RouteNotification routeNotification) {
      return RouteNotificationResponse.builder()
          .id(routeNotification.getId())
          .memberId(routeNotification.getMemberId())
          .createdAt(routeNotification.getCreatedAt())
          .confirmation(routeNotification.isConfirmed())
          .title(routeNotification.getTitle())
          .routeId(routeNotification.getRouteId())
          .build();
    } else {
      throw new IllegalArgumentException("Unknown notification type: " + notification.getClass());
    }
  }
}
