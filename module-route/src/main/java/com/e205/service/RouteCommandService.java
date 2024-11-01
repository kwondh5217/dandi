package com.e205.service;

import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.exception.RouteError;
import com.e205.exception.RouteException;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.query.BagItemsOfMemberQuery;
import com.e205.repository.RouteRepository;
import com.e205.service.validator.RouteValidator;
import com.e205.util.GeometryUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RouteCommandService {

  private final RouteRepository routeRepository;
  private final RouteValidator routeValidator;
  private final BagItemQueryService bagItemQueryService;

  public void createRoute(RouteCreateCommand command, Integer memberId) {
    Optional<Route> route = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId);
    String determinedSnapshot = route.map(findRoute -> {
          Snapshot defaultSnapshot = loadBaseSnapshot(command, memberId);
          Snapshot currentSnapshot = loadCurrentSnapshot(findRoute);
          return Route.determineSnapshot(command, defaultSnapshot, currentSnapshot);
        })
        .orElseGet(() -> {
          return Snapshot.toJson(loadBaseSnapshot(command, memberId));
        });
    routeRepository.save(Route.toEntity(memberId, command, determinedSnapshot));
  }

  private Snapshot loadBaseSnapshot(RouteCreateCommand request, Integer memberId) {
    BagItemsOfMemberQuery query = new BagItemsOfMemberQuery(memberId, request.bagId());
    List<SnapshotItem> snapshotItems = bagItemQueryService.bagItemsOfMember(query);
    return new Snapshot(request.bagId(), snapshotItems);
  }

  private Snapshot loadCurrentSnapshot(Route previousRoute) {
    return Snapshot.fromJson(previousRoute.getSnapshot());
  }

  public void updateSnapshot(SnapshotUpdateCommand command) {
    Route route = getRoute(command.routeId());
    routeValidator.validateEndedRoute(route);
    route.updateSnapshot(Snapshot.toJson(command.snapshot()));
    routeRepository.save(route);
  }

  public void endRoute(RouteEndCommand command) {
    Route route = getRoute(command.routeId());
    routeValidator.validateEndedRoute(route);
    route.endRoute(GeometryUtils.getLineString(command.points()), command.endedAt());
    routeRepository.save(route);
  }

  private Route getRoute(Integer routeId) {
    return routeRepository.findById(routeId)
        .orElseThrow(() -> new RouteException(RouteError.NOT_FOUND_ROUTE));
  }
}
