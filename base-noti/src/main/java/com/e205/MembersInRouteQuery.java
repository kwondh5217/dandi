package com.e205;

import com.e205.querys.Query;
import java.time.LocalDateTime;
import org.locationtech.jts.geom.LineString;

public record MembersInRouteQuery(
    LineString route,
    LocalDateTime since,
    LocalDateTime until
) implements Query {

  @Override
  public String getType() {
    return "membersInPointsQuery";
  }
}


