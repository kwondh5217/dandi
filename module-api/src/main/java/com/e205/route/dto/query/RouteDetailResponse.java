package com.e205.route.dto.query;

import com.e205.dto.Snapshot;
import com.e205.dto.TrackPoint;
import com.e205.payload.RoutePayload;
import com.e205.route.dto.Point;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record RouteDetailResponse(
    Integer id,
    Integer memberId,
    List<Point> track,
    char skip,
    Snapshot startSnapshot,
    Snapshot nextSnapshot,
    Integer previousRouteId,
    Integer nextRouteId,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

  public static RouteDetailResponse fromPayload(RoutePayload payload) {
    return RouteDetailResponse.builder()
        .id(payload.id())
        .memberId(payload.memberId())
        .track(toPoints(payload.track()))
        .skip(payload.skip())
        .startSnapshot(payload.startSnapshot())
        .nextSnapshot(payload.nextSnapshot())
        .previousRouteId(payload.previousRouteId())
        .nextRouteId(payload.nextRouteId())
        .createdAt(payload.createdAt())
        .endedAt(payload.endedAt())
        .build();
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
