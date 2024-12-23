package com.e205.base.item.event;

import com.e205.events.Event;
import com.e205.base.item.payload.FoundItemPayload;
import java.time.LocalDateTime;

public record FoundItemSaveEvent(
    FoundItemPayload saved,
    LocalDateTime publishedAt
) implements Event {

  @Override
  public String getType() {
    return "foundItem";
  }
}
