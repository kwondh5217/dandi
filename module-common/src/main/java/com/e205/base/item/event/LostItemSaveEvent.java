package com.e205.base.item.event;

import com.e205.base.item.payload.LostItemPayload;
import com.e205.events.Event;
import java.time.LocalDateTime;

public record LostItemSaveEvent(
    LostItemPayload saved,
    LocalDateTime publishedAt
) implements Event {

  @Override
  public String getType() {
    return "lostItem";
  }
}
