package com.e205.command.bag.event;

import com.e205.events.Event;

public record BagChangedEvent(
    Integer memberId,
    Integer bagId
) implements Event {

  @Override
  public String getType() {
    return "bagChanged";
  }
}
