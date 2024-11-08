package com.e205.route.service;

import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.payload.RoutePayload;
import com.e205.payload.RoutesPayload;
import com.e205.payload.SnapshotPayload;
import com.e205.query.DailyRouteReadQuery;
import com.e205.query.RouteReadQuery;
import com.e205.query.SnapshotReadQuery;
import com.e205.route.dto.command.RouteCreateRequest;
import com.e205.route.dto.command.RouteEndRequest;
import com.e205.route.dto.command.SnapshotUpdateRequest;
import com.e205.route.dto.query.DailyRouteResponse;
import com.e205.route.dto.query.RouteDetailResponse;
import com.e205.route.dto.query.SnapshotDetailResponse;
import com.e205.service.RouteCommandService;
import com.e205.service.RouteQueryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RouteService {

  private final RouteCommandService commandService;
  private final RouteQueryService queryService;

  public void createRoute(RouteCreateRequest request, Integer memberId) {
    RouteCreateCommand comm = new RouteCreateCommand(request.bagId(), memberId);
    commandService.createRoute(comm);
  }

  public void updateSnapshot(Integer memberId, Integer routeId, SnapshotUpdateRequest request) {
    SnapshotUpdateCommand comm = new SnapshotUpdateCommand(memberId, routeId, request.snapshot());
    commandService.updateSnapshot(comm);
  }

  public void endRoute(Integer memberId, Integer routeId, RouteEndRequest request) {
    RouteEndCommand comm = RouteEndRequest.toCommand(memberId, routeId, request);
    commandService.endRoute(comm);
  }

  public DailyRouteResponse readDailyRoute(Integer memberId, LocalDate date) {
    DailyRouteReadQuery query = new DailyRouteReadQuery(memberId, date);
    RoutesPayload routesPayload = queryService.readDailyRoute(query);
    return DailyRouteResponse.fromPayload(routesPayload);
  }

  public RouteDetailResponse readRoute(Integer memberId, Integer routeId) {
    RouteReadQuery query = new RouteReadQuery(memberId, routeId);
    RoutePayload routePayload = queryService.readRoute(query);
    return RouteDetailResponse.fromPayload(routePayload);
  }

  public SnapshotDetailResponse readSnapshot(Integer memberId, Integer routeId) {
    SnapshotReadQuery query = new SnapshotReadQuery(memberId, routeId);
    SnapshotPayload routePayload = queryService.readSnapshot(query);
    return SnapshotDetailResponse.fromPayload(routePayload);
  }
}
