package com.e205.util;

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
}
