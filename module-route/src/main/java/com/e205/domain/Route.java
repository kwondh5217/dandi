package com.e205.domain;

import com.e205.command.RouteCreateCommand;
import com.e205.dto.RoutePart;
import com.e205.dto.Snapshot;
import com.e205.dto.TrackPoint;
import com.e205.log.LoggableEntity;
import com.e205.payload.RoutePayload;
import com.e205.util.GeometryUtils;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

  public void endRoute(LineString track) {
    this.track = track;
    this.endedAt = LocalDateTime.now();
  }

  public void updateSnapshot(String snapshot) {
    this.skip = 'N';
    this.snapshot = snapshot;
  }

  public static Route toEntity(Integer memberId, String snapshot) {
    return Route.builder()
        .memberId(memberId)
        .skip('Y')
        .snapshot(snapshot)
        .createdAt(LocalDateTime.now())
        .build();
  }

  public static RoutePayload toPayload(Route route, Snapshot nextSnapshot,
      Integer previousRouteId, Integer nextRouteId
  ) {
    return RoutePayload.builder()
        .id(route.id)
        .memberId(route.memberId)
        .track(toTrackPoints(route.track))
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
    List<TrackPoint> trackPoints = new ArrayList<>();

    if (route.getTrack() != null) {
      trackPoints = Arrays.stream(route.getTrack().getCoordinates())
          .map(coord -> new TrackPoint(coord.getY(), coord.getX()))
          .collect(Collectors.toList());
    }

    return RoutePart.builder()
        .id(route.getId())
        .track(trackPoints)
        .createdAt(route.getCreatedAt())
        .endedAt(route.getEndedAt())
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

  public static List<TrackPoint> toTrackPoints(LineString lineString) {
    return GeometryUtils.toTrackPoints(lineString);
  }
}
