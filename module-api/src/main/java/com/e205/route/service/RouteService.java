package com.e205.route.service;

import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.geo.dto.Point;
import com.e205.geo.service.VwolrdGeoClient;
import com.e205.payload.RouteIdPayload;
import com.e205.payload.RoutePayload;
import com.e205.payload.RoutesPayload;
import com.e205.payload.SnapshotPayload;
import com.e205.query.CurrentRouteReadQuery;
import com.e205.query.DailyRouteReadQuery;
import com.e205.query.RouteReadQuery;
import com.e205.query.SnapshotReadQuery;
import com.e205.route.dto.command.RouteCreateRequest;
import com.e205.route.dto.command.RouteEndRequest;
import com.e205.route.dto.command.SnapshotUpdateRequest;
import com.e205.route.dto.query.CurrentRouteIdResponse;
import com.e205.route.dto.query.DailyRouteResponse;
import com.e205.route.dto.query.RouteDetailResponse;
import com.e205.route.dto.query.SnapshotDetailResponse;
import com.e205.service.RouteCommandService;
import com.e205.service.RouteQueryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RouteService {

  private final RouteCommandService commandService;
  private final RouteQueryService queryService;
  private final VwolrdGeoClient vwolrdGeoClient;

  public void createRoute(RouteCreateRequest request, Integer memberId) {
    RouteCreateCommand comm = new RouteCreateCommand(memberId, request.bagId());
    commandService.createRoute(comm);
  }

  public void updateSnapshot(Integer memberId, Integer routeId, SnapshotUpdateRequest request) {
    SnapshotUpdateCommand comm = new SnapshotUpdateCommand(memberId, routeId, request.snapshot());
    commandService.updateSnapshot(comm);
  }

  public void endRoute(Integer memberId, Integer routeId, RouteEndRequest request) {
    List<Point> track = request.track();
    String start = vwolrdGeoClient.findFullAddress(track.get(0));
    String end = vwolrdGeoClient.findFullAddress(track.get(track.size() - 1));

    RouteEndCommand comm = RouteEndRequest.toCommand(memberId, routeId, request, start, end);
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

  public CurrentRouteIdResponse readCurrentRouteId(Integer memberId) {
    CurrentRouteReadQuery query = new CurrentRouteReadQuery(memberId);
    RouteIdPayload routeIdPayload = queryService.readCurrentRouteId(query);
    return new CurrentRouteIdResponse(routeIdPayload.routeId());
  }
}
