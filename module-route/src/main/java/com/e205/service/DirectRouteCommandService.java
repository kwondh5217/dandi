package com.e205.service;

import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.domain.Route;
import com.e205.dto.RouteEventPayload;
import com.e205.dto.Snapshot;
import com.e205.event.RouteSavedEvent;
import com.e205.events.EventPublisher;
import com.e205.exception.GlobalException;
import com.e205.repository.RouteRepository;
import com.e205.service.reader.SnapshotHelper;
import com.e205.util.GeometryUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DirectRouteCommandService implements RouteCommandService {

  private final EventPublisher eventPublisher;
  private final RouteRepository routeRepository;
  private final SnapshotHelper snapshotHelper;

  @Override
  public void createRoute(RouteCreateCommand command) {
    Integer memberId = command.memberId();
    Integer bagId = command.bagId();
    Optional<Route> route = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId);

    Snapshot currentSnapshot = route.map(snapshotHelper::loadCurrentSnapshot).orElse(null);
    Snapshot baseSnapshot = snapshotHelper.loadBaseSnapshot(memberId, bagId);

    String determinedSnapshot = SnapshotHelper.mergeSnapshots(baseSnapshot, currentSnapshot);
    Route savedRoute = routeRepository.save(Route.toEntity(memberId, determinedSnapshot));

    String payload = RouteEventPayload.toJson(getPayload(savedRoute, determinedSnapshot));
    eventPublisher.publishAtLeastOnce(new RouteSavedEvent(memberId, payload));
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
    routeRepository.save(route);
  }

  @Override
  public void endRoute(RouteEndCommand command) {
    Route route = getRoute(command.routeId(), command.memberId());
    if (route.getEndedAt() != null) {
      throw new GlobalException("E202");
    }
    route.endRoute(
        GeometryUtils.getLineString(command.points()),
        command.startAddress(),
        command.endAddress()
    );
    routeRepository.save(route);
  }

  private Route getRoute(Integer routeId, Integer memberId) {
    return routeRepository.findByIdAndMemberId(routeId, memberId)
        .orElseThrow(() -> new GlobalException("E201"));
  }
}
