package com.e205;

import com.e205.querys.Query;

public record QueryNotificationWithCursor(
    Integer memberId,
    Integer lastResourceId
) implements Query {

  @Override
  public String getType() {
    return "findNotificationWithCursor";
  }
}
