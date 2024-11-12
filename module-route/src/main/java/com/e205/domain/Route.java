package com.e205.domain;

import com.e205.dto.RoutePart;
import com.e205.dto.Snapshot;
import com.e205.dto.TrackPoint;
import com.e205.log.LoggableEntity;
import com.e205.payload.RoutePayload;
import com.e205.util.GeometryUtils;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private LineString track;
  @Column(length = 1)
  private char skip;
  @Column(length = 2000)
  private String snapshot;
  @Column(length = 100)
  private String startAddress;
  @Column(length = 100)
  private String endAddress;
  private LocalDateTime createdAt;
  private LocalDateTime endedAt;

  public void updateTrack(LineString track) {
    this.track = track;
  }

  public void endRoute(LineString track, String startAddress, String endAddress) {
    this.track = track;
    this.startAddress = startAddress;
    this.endAddress = endAddress;
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
        .startAddress(route.startAddress)
        .endAddress(route.endAddress)
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

    if (route.track != null) {
      trackPoints = Arrays.stream(route.track.getCoordinates())
          .map(coord -> new TrackPoint(coord.getY(), coord.getX()))
          .collect(Collectors.toList());
    }

    return RoutePart.builder()
        .id(route.id)
        .startAddress(route.startAddress)
        .endAddress(route.endAddress)
        .track(trackPoints)
        .createdAt(route.createdAt)
        .endedAt(route.endedAt)
        .build();
  }

  public static List<TrackPoint> toTrackPoints(LineString lineString) {
    return GeometryUtils.toTrackPoints(lineString);
  }
}
