package com.e205;

import com.e205.querys.Query;
import java.time.LocalDateTime;

public record MembersInRouteQuery(
    Integer startRouteId,
    Integer endRouteId,
    LocalDateTime since,
    LocalDateTime until
) implements Query {

  @Override
  public String getType() {
    return "membersInPointsQuery";
  }
}


