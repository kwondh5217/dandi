package com.e205.message;

import com.e205.entity.LostItem;
import com.e205.events.Event;
import java.time.LocalDateTime;

public record LostItemSaveEvent(
    LostItem saved,
    LocalDateTime publishedAt
) implements Event {

  @Override
  public String getType() {
    return "lostItemSaveEvent";
  }
}
