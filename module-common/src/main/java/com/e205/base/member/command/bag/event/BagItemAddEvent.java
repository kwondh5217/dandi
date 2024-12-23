package com.e205.base.member.command.bag.event;

import com.e205.base.member.command.item.payload.ItemPayload;
import com.e205.events.Event;

public record BagItemAddEvent(
    ItemPayload itemPayload
) implements Event {

  @Override
  public String getType() {
    return "bagItemAdd";
  }
}
