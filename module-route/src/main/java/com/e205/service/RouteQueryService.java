package com.e205.service;

import com.e205.domain.Route;
import com.e205.dto.RoutePart;
import com.e205.dto.Snapshot;
import com.e205.exception.RouteError;
import com.e205.exception.RouteException;
import com.e205.payload.RoutePayload;
import com.e205.payload.RoutesPayload;
import com.e205.payload.SnapshotPayload;
import com.e205.query.DailyRouteReadQuery;
import com.e205.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import com.e205.util.GeometryUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RouteQueryService {

  private final RouteRepository routeRepository;
  private final GeometryUtils geometryUtils;
  @Value("${route.max-radius}")
  private double radius;

  public SnapshotPayload readSnapshot(Integer routeId) {
    Route route = getRoute(routeId);

    Snapshot snapshot = Snapshot.fromJson(route.getSnapshot());
    char skip = route.getSkip();

    return SnapshotPayload.builder()
        .snapshot(snapshot)
        .skip(skip)
        .build();
  }

  public RoutePayload readRoute(RouteReadQuery query) {
    Integer routeId = query.routeId();
    Route currentRoute = getRoute(routeId);

    String nextSnapshot = getNextRoute(currentRoute.getMemberId(), routeId)
        .filter(nextRoute -> isWithinDistance(currentRoute, nextRoute))
        .map(Route::getSnapshot)
        .orElse(null);

    return Route.toPayload(currentRoute, nextSnapshot);
  }

  public RoutesPayload readSpecificDayRoutes(DailyRouteReadQuery query) {
    Integer memberId = query.memberId();
    List<Route> routes = routeRepository.findAllByMemberIdAndCreatedAtDate(memberId, query.date());
    List<RoutePart> routeParts = routes.stream().map(Route::toPart).toList();

    if (routes.isEmpty()) {
      return null;
    }

    Route lastRoute = routes.get(routes.size() - 1);
    Integer nextRouteId = getNextRoute(memberId, lastRoute.getId())
        .filter(nextRoute -> isWithinDistance(lastRoute, nextRoute))
        .map(Route::getId)
        .orElse(null);

    return RoutesPayload.builder().routeParts(routeParts).nextRouteId(nextRouteId).build();
  }

  private Route getRoute(Integer routeId) {
    return routeRepository.findById(routeId)
        .orElseThrow(() -> new RouteException(RouteError.NOT_FOUND_ROUTE));
  }

  private Optional<Route> getNextRoute(Integer memberId, Integer routeId) {
    return routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(memberId, routeId);
  }

  private boolean isWithinDistance(Route nextRoute, Route lastRoute) {
    return geometryUtils.isWithinDistance(
        lastRoute.getTrack(), nextRoute.getTrack(), radius);
  }
}
