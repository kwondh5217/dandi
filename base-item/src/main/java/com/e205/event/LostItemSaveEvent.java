package com.e205.event;

import com.e205.events.Event;
import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;

public record LostItemSaveEvent(
    LostItemPayload saved,
    LocalDateTime publishedAt
) implements Event {

  @Override
  public String getType() {
    return "lostItemSaveEvent";
  }
}
