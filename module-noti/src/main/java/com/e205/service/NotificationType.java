package com.e205.service;

public enum NotificationType {
  LOST_ITEM("lostItem"),
  FOUND_ITEM("foundItem"),
  FOUND_COMMENT("foundComment"),
  LOST_COMMENT("lostComment"),
  ROUTE("route"),
  COMMENT("comment");

  private final String type;

  NotificationType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static NotificationType fromString(final String type) {
    for (NotificationType notificationType : NotificationType.values()) {
      if (notificationType.getType().equals(type)) {
        return notificationType;
      }
    }
    throw new IllegalArgumentException("Unknown notification type: " + type);
  }
}