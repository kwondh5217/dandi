package com.e205.service;

import com.e205.base.route.command.RouteCreateCommand;
import com.e205.base.route.command.RouteEndCommand;
import com.e205.base.route.command.SnapshotUpdateCommand;
import com.e205.base.route.service.RouteCommandService;
import com.e205.domain.Route;
import com.e205.dto.RouteEventPayload;
import com.e205.base.route.dto.Snapshot;
import com.e205.base.route.event.RouteSavedEvent;
import com.e205.exception.GlobalException;
import com.e205.repository.RouteRepository;
import com.e205.service.reader.SnapshotHelper;
import com.e205.util.GeometryUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DirectRouteCommandService implements RouteCommandService {

  private final ApplicationEventPublisher eventPublisher;
  private final RouteRepository routeRepository;
  private final SnapshotHelper snapshotHelper;
  private final GeometryUtils geometryUtils;

  @Override
  public void createRoute(RouteCreateCommand command) {
    Integer memberId = command.memberId();
    Integer bagId = command.bagId();
    Optional<Route> route = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId);

    Snapshot currentSnapshot = route.map(snapshotHelper::loadCurrentSnapshot).orElse(null);
    Snapshot baseSnapshot = snapshotHelper.loadBaseSnapshot(memberId, bagId);

    String determinedSnapshot = SnapshotHelper.mergeSnapshots(baseSnapshot, currentSnapshot);
    Route initialRoute = Route.toEntity(
        memberId,
        determinedSnapshot,
        geometryUtils.createEmptyLineString(),
        geometryUtils.createEmptyPolygon()
    );

    Route savedRoute = routeRepository.save(initialRoute);
    String payload = RouteEventPayload.toJson(getPayload(savedRoute, determinedSnapshot));
    eventPublisher.publishEvent(new RouteSavedEvent(memberId, payload));
  }

  private RouteEventPayload getPayload(Route savedRoute, String determinedSnapshot) {
    return RouteEventPayload.builder()
        .routeId(savedRoute.getId())
        .skip(savedRoute.getSkip())
        .snapshot(Snapshot.fromJson(determinedSnapshot))
        .build();
  }

  @Override
  public void updateSnapshot(SnapshotUpdateCommand command) {
    Route route = getRoute(command.routeId(), command.memberId());
    route.updateSnapshot(Snapshot.toJson(command.snapshot()));
  }

  @Override
  public void endRoute(RouteEndCommand command) {
    Route route = getRoute(command.routeId(), command.memberId());
    if (route.getEndedAt() != null) {
      throw new GlobalException("E202");
    }

    LineString track = geometryUtils.getLineString(command.points());
    LineString filteredTrack = geometryUtils.filterTrackPoints(track);
    Polygon radiusTrack = geometryUtils.createLineCirclePolygon(filteredTrack);

    route.endRoute(
        track,
        radiusTrack,
        command.startAddress(),
        command.endAddress()
    );
  }

  private Route getRoute(Integer routeId, Integer memberId) {
    return routeRepository.findByIdAndMemberId(routeId, memberId)
        .orElseThrow(() -> new GlobalException("E201"));
  }
}
