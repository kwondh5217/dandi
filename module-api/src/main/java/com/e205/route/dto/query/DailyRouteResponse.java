package com.e205.route.dto.query;

import com.e205.dto.TrackPoint;
import com.e205.geo.dto.Point;
import com.e205.payload.RoutesPayload;
import com.e205.route.dto.RouteSummary;
import java.util.List;
import java.util.stream.Collectors;

public record DailyRouteResponse(
    List<RouteSummary> routes,
    Integer nextRouteId
) {

  public static DailyRouteResponse fromPayload(RoutesPayload payload) {
    List<RouteSummary> routeInfos = payload.routeParts().stream()
        .map(routePart -> RouteSummary.builder()
            .id(routePart.id())
            .startAddress(routePart.startAddress())
            .endAddress(routePart.endAddress())
            .track(toPoints(routePart.track()))
            .createdAt(routePart.createdAt())
            .endedAt(routePart.endedAt())
            .build()
        )
        .collect(Collectors.toList());

    return new DailyRouteResponse(routeInfos, payload.nextRouteId());
  }

  private static List<Point> toPoints(List<TrackPoint> track) {
    if (track == null) {
      return null;
    }
    return track.stream()
        .map(coord -> new Point(coord.lat(), coord.lon()))
        .collect(Collectors.toList());
  }
}
