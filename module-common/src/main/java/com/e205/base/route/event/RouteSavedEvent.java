package com.e205.base.route.event;

import com.e205.events.Event;

public record RouteSavedEvent(
    Integer memberId,
    String payload // routeId, snapshot, skip 데이터 포함 
) implements Event {

  @Override
  public String getType() {
    return "route";
  }
}
