package com.e205.route.dto.command;

import com.e205.base.route.command.RouteEndCommand;
import com.e205.base.route.dto.TrackPoint;
import com.e205.geo.dto.Point;
import java.util.List;

public record RouteEndRequest(
    List<Point> track
) {

  public static RouteEndCommand toCommand(Integer memberId, Integer routeId, RouteEndRequest req,
      String startAddress, String endAddress
  ) {
    return RouteEndCommand.builder()
        .memberId(memberId)
        .routeId(routeId)
        .points(req.track().stream()
            .map(point -> TrackPoint.builder()
                .lat(point.lat())
                .lon(point.lon())
                .build())
            .toList())
        .startAddress(startAddress)
        .endAddress(endAddress)
        .build();
  }
}
