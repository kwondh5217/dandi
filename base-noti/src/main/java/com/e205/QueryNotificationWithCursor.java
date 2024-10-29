package com.e205;

import com.e205.querys.Query;
import java.util.List;

public record QueryNotificationWithCursor(
    Integer memberId,
    Integer lastResourceId,
    List<String> types
) implements Query {

  @Override
  public String getType() {
    return "findNotificationWithCursor";
  }
}
