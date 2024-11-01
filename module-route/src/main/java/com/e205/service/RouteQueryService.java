package com.e205.service;

import com.e205.domain.Route;
import com.e205.payload.RoutePayload;
import com.e205.exception.RouteError;
import com.e205.exception.RouteException;
import com.e205.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RouteQueryService {

  private final RouteRepository routeRepository;

  public RoutePayload readRoute(RouteReadQuery query) {
    Integer routeId = query.routeId();
    Route currentRoute = getRoute(routeId);

    String nextSnapshot = getNextRoute(currentRoute.getMemberId(), routeId)
        .map(Route::getSnapshot)
        .orElse(null);
    // TODO <이현수> : 거리에 따른 End 스냅샷 반환

    return Route.toPayload(currentRoute, nextSnapshot);
  }

  private Route getRoute(Integer routeId) {
    return routeRepository.findById(routeId)
        .orElseThrow(() -> new RouteException(RouteError.NOT_FOUND_ROUTE));
  }

  private Optional<Route> getNextRoute(Integer memberId, Integer routeId) {
    return routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(memberId, routeId);
  }
}
