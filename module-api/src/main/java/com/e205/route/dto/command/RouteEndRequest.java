package com.e205.route.dto.command;

import com.e205.command.RouteEndCommand;
import com.e205.dto.TrackPoint;
import com.e205.route.dto.Point;
import java.util.List;

public record RouteEndRequest(
    List<Point> track
) {

  public static RouteEndCommand toCommand(Integer routeId, RouteEndRequest request) {
    return RouteEndCommand.builder()
        .routeId(routeId)
        .points(request.track().stream()
            .map(point -> TrackPoint.builder()
                .lat(point.lat())
                .lon(point.lon())
                .build())
            .toList())
        .build();
  }
}
