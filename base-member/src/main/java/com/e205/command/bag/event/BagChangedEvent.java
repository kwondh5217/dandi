package com.e205.command.bag.event;

import com.e205.command.item.payload.ItemPayload;
import com.e205.events.Event;
import java.util.List;

public record BagChangedEvent(
    List<ItemPayload> itemPayloads
) implements Event {

  @Override
  public String getType() {
    return "bagChanged";
  }
}
