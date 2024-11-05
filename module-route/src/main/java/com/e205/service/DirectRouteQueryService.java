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
import com.e205.query.MembersInRouteQuery;
import com.e205.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import com.e205.util.GeometryUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DirectRouteQueryService implements RouteQueryService {

  @Value("${route.max-radius}")
  private double radius;

  private final RouteRepository routeRepository;
  private final GeometryUtils geometryUtils;

  @Override
  public SnapshotPayload readSnapshot(Integer routeId) {
    Route route = getRoute(routeId);

    Snapshot snapshot = Snapshot.fromJson(route.getSnapshot());
    char skip = route.getSkip();

    return SnapshotPayload.builder()
        .snapshot(snapshot)
        .skip(skip)
        .build();
  }

  @Override
  public RoutePayload readRoute(RouteReadQuery query) {
    Integer routeId = query.routeId();
    Integer memberId = query.memberId();
    Route currentRoute = getRoute(routeId);

    Integer previousId = getPreviousRoute(memberId, routeId)
        .map(Route::getId)
        .orElse(null);

    Optional<Route> nextRoute = getNextRoute(memberId, routeId);
    Integer nextRouteId = nextRoute.map(Route::getId).orElse(null);
    Snapshot nextSnapshot = Snapshot.fromJson(nextRoute
        .filter((route) -> isWithinDistance(route, currentRoute))
        .map(Route::getSnapshot)
        .orElse(null));

    return Route.toPayload(currentRoute, nextSnapshot, previousId, nextRouteId);
  }

  @Override
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

  @Override
  public List<Integer> findUserIdsNearPath(MembersInRouteQuery query) {
    List<Route> routesInRange = routeRepository.findRoutesWithinRange(
        query.startRouteId(), query.endRouteId()
    );

    LineString combinedTrack = geometryUtils.combineTracks(
        routesInRange.stream().map(Route::getTrack).collect(Collectors.toList())
    );

    Polygon bufferedPolygon = geometryUtils.createBufferedPolygon(combinedTrack, radius);

    return new ArrayList<>(routeRepository.findUsersWithinPolygon(bufferedPolygon, query.since()));
  }

  private Route getRoute(Integer routeId) {
    return routeRepository.findById(routeId)
        .orElseThrow(() -> new RouteException(RouteError.NOT_FOUND_ROUTE));
  }

  private Optional<Route> getPreviousRoute(Integer memberId, Integer routeId) {
    return routeRepository.findFirstByMemberIdAndIdIsLessThanOrderByIdDesc(memberId, routeId);
  }

  private Optional<Route> getNextRoute(Integer memberId, Integer routeId) {
    return routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(memberId, routeId);
  }

  private boolean isWithinDistance(Route nextRoute, Route lastRoute) {
    return geometryUtils.isWithinDistance(
        lastRoute.getTrack(), nextRoute.getTrack(), radius);
  }
}
