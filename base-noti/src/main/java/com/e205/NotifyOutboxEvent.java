package com.e205;

import com.e205.events.Event;

public record NotifyOutboxEvent(
    String fcmToken, String notiType, String title)
    implements Event {

  @Override
  public String getType() {
    return "notifyEvent";
  }
}
