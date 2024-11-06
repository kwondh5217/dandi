package com.e205.route.controller;

import com.e205.auth.helper.AuthHelper;
import com.e205.route.dto.command.RouteCreateRequest;
import com.e205.route.dto.command.RouteEndRequest;
import com.e205.route.dto.command.SnapshotUpdateRequest;
import com.e205.route.dto.query.DailyRouteResponse;
import com.e205.route.dto.query.RouteDetailResponse;
import com.e205.route.dto.query.SnapshotDetailResponse;
import com.e205.route.service.RouteService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/routes")
@RestController
public class RouteController {

  private final AuthHelper authHelper;
  private final RouteService routeService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createRoute(@RequestBody RouteCreateRequest request) {
    routeService.createRoute(request, authHelper.getMemberId());
  }

  @PatchMapping("/{routeId}/snapshot")
  @ResponseStatus(HttpStatus.OK)
  public void updateSnapshot(
      @PathVariable Integer routeId,
      @RequestBody SnapshotUpdateRequest request
  ) {
    routeService.updateSnapshot(routeId, request);
  }

  @PatchMapping("/{routeId}")
  @ResponseStatus(HttpStatus.OK)
  public void endRoute(@PathVariable Integer routeId, @RequestBody RouteEndRequest request) {
    routeService.endRoute(routeId, request);
  }

  @GetMapping
  public ResponseEntity<DailyRouteResponse> readSpecificDayRoutes(
      @RequestParam("date") LocalDate date
  ) {
    Integer memberId = authHelper.getMemberId();
    DailyRouteResponse response = routeService.readDailyRoute(memberId, date);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{routeId}")
  public ResponseEntity<RouteDetailResponse> readRoute(@PathVariable Integer routeId) {
    Integer memberId = authHelper.getMemberId();
    RouteDetailResponse response = routeService.readRoute(memberId, routeId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{routeId}/snapshot")
  public ResponseEntity<SnapshotDetailResponse> readRouteSnapshot(@PathVariable Integer routeId) {
    SnapshotDetailResponse response = routeService.readSnapshot(routeId);
    return ResponseEntity.ok(response);
  }
}