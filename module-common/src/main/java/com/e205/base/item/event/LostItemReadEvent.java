package com.e205.base.item.event;

import com.e205.events.Event;
import java.time.LocalDateTime;

public record LostItemReadEvent(
    Integer lostItemId,
    LocalDateTime publishedAt
) implements Event {

  @Override
  public String getType() {
    return "lostItemReadEvent";
  }
}
