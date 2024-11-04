package com.e205.service;

import com.e205.payload.RoutePayload;
import com.e205.payload.RoutesPayload;
import com.e205.payload.SnapshotPayload;
import com.e205.query.DailyRouteReadQuery;
import com.e205.query.MembersInRouteQuery;
import com.e205.query.RouteReadQuery;
import java.util.List;

public interface RouteQueryService {

  /**
   * 특정 이동 ID에 대한 스냅샷 정보를 조회합니다.
   *
   * @param routeId 조회할 이동의 ID
   * @return SnapshotPayload 스냅샷 정보와 건너뛰기 여부
   */
  SnapshotPayload readSnapshot(Integer routeId);

  /**
   * 특정 이동의 상세 정보를 조회합니다.
   *
   * @param query 이동 조회 요청 쿼리
   * @return RoutePayload 경로의 상세 정보
   */
  RoutePayload readRoute(RouteReadQuery query);

  /**
   * 특정 날짜의 이동들을 조회합니다.
   *
   * @param query 특정 날짜에 대한 이동 조회 요청 쿼리
   * @return RoutesPayload 이동 목록 및 다음 이동 ID 정보
   */
  RoutesPayload readSpecificDayRoutes(DailyRouteReadQuery query);

  /**
   * 분실물 경로 반경 내 사용자들의 ID 목록을 조회합니다.
   *
   * @param query 이동들의 경로 내 사용자 요청 쿼리
   * @return List<Integer> 분실물 경로 반경 내 사용자 ID 목록
   */
  List<Integer> findUserIdsNearPath(MembersInRouteQuery query);
}