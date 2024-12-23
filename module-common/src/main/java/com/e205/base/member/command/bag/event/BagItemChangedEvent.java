package com.e205.base.member.command.bag.event;

import com.e205.base.member.command.item.payload.ItemPayload;
import com.e205.events.Event;

public record BagItemChangedEvent(
    ItemPayload previousItemPayload,
    ItemPayload itemPayload
) implements Event {

  @Override
  public String getType() {
    return "bagItemChanged";
  }
}
