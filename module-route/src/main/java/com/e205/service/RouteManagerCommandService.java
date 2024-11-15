package com.e205.service;

import com.e205.command.RouteDummyCreateCommand;
import com.e205.domain.Route;
import com.e205.repository.RouteRepository;
import com.e205.util.GeometryUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RouteManagerCommandService implements RouteDummyCommandService {

  private final RouteRepository routeRepository;
  private final GeometryUtils geometryUtils;

  @Override
  public void createRouteDummy(RouteDummyCreateCommand command) {
    LineString track = geometryUtils.getLineString(command.track());
    LineString filteredTrack = geometryUtils.filterTrackPoints(track);
    Polygon radiusTrack = geometryUtils.createLineCirclePolygon(filteredTrack);

    routeRepository.save(Route.builder()
        .memberId(command.memberId())
        .track(track)
        .radiusTrack(radiusTrack)
        .skip('N')
        .snapshot(command.snapshot())
        .startAddress(command.startAddress())
        .endAddress(command.endAddress())
        .createdAt(LocalDateTime.now().minusMinutes(30))
        .endedAt(LocalDateTime.now())
        .build()
    );
  }
}
