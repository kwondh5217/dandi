package com.e205.domain;

import com.e205.command.RouteCreateCommand;
import com.e205.dto.RoutePart;
import com.e205.dto.Snapshot;
import com.e205.log.LoggableEntity;
import com.e205.payload.RoutePayload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Route implements LoggableEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;
  private Integer memberId;
  private LineString track;
  @Column(length = 1)
  private char skip;
  @Column(length = 2000)
  private String snapshot;
  private LocalDateTime createdAt;
  private LocalDateTime endedAt;

  public void updateTrack(LineString track) {
    this.track = track;
  }

  public void endRoute(LineString track, LocalDateTime endedAt) {
    this.track = track;
    this.endedAt = endedAt;
  }

  public void updateSnapshot(String snapshot) {
    this.skip = 'N';
    this.snapshot = snapshot;
  }

  public static Route toEntity(Integer memberId, RouteCreateCommand request, String snapshot) {
    return Route.builder()
        .memberId(memberId)
        .skip('Y')
        .snapshot(snapshot)
        .createdAt(request.createdAt())
        .build();
  }

  public static RoutePayload toPayload(Route route, Snapshot nextSnapshot,
      Integer previousRouteId, Integer nextRouteId
  ) {
    return RoutePayload.builder()
        .id(route.id)
        .memberId(route.memberId)
        .track(route.track)
        .skip(route.skip)
        .startSnapshot(Snapshot.fromJson(route.snapshot))
        .nextSnapshot(nextSnapshot)
        .previousRouteId(previousRouteId)
        .nextRouteId(nextRouteId)
        .createdAt(route.createdAt)
        .endedAt(route.endedAt)
        .build();
  }

  public static RoutePart toPart(Route route) {
    return RoutePart.builder()
        .id(route.id)
        .track(route.track)
        .createdAt(route.createdAt)
        .endedAt(route.endedAt)
        .build();
  }

  public static String determineSnapshot(RouteCreateCommand request, Snapshot ss, Snapshot currentSs
  ) {
    if (Objects.equals(request.bagId(), currentSs.bagId())) {
      return Snapshot.toJson(currentSs);
    } else {
      return Snapshot.toJson(ss);
    }
  }
}
