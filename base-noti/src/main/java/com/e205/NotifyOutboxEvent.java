package com.e205;

import com.e205.events.Event;
import lombok.NonNull;

public record NotifyOutboxEvent(
    @NonNull String fcmToken,
    @NonNull String notiType,
    String title
) implements Event {

  @Override
  public String getType() {
    return "notifyEvent";
  }
}
