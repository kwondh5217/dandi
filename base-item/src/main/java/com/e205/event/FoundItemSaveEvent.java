package com.e205.event;

import com.e205.events.Event;
import com.e205.payload.FoundItemPayload;
import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;

public record FoundItemSaveEvent(
    FoundItemPayload saved,
    LocalDateTime publishedAt
) implements Event {

  @Override
  public String getType() {
    return "foundItemSaveEvent";
  }
}
