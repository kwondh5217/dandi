package com.e205.event;

import com.e205.events.Event;

public record RouteSavedEvent(
    Integer routeId,
    Integer memberId,
    String snapShot
) implements Event {

  @Override
  public String getType() {
    return "routeSaved";
  }
}
