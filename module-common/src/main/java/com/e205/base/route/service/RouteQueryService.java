package com.e205.base.route.service;

import com.e205.base.route.payload.RoutePayload;
import com.e205.base.route.payload.SnapshotPayload;
import com.e205.base.route.query.MembersInPointQuery;
import com.e205.base.route.query.MembersInRouteQuery;
import com.e205.base.route.query.RouteReadQuery;
import com.e205.base.route.query.SnapshotReadQuery;
import com.e205.base.route.payload.RouteIdPayload;
import com.e205.base.route.payload.RoutesPayload;
import com.e205.base.route.query.CurrentRouteReadQuery;
import com.e205.base.route.query.DailyRouteReadQuery;
import java.util.List;

public interface RouteQueryService {

  /**
   * 특정 이동 ID에 대한 스냅샷 정보를 조회합니다.
   *
   * @param query 스냅샷 조회 요청 쿼리
   * @return SnapshotPayload 스냅샷 정보와 건너뛰기 여부
   */
  SnapshotPayload readSnapshot(SnapshotReadQuery query);

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
  RoutesPayload readDailyRoute(DailyRouteReadQuery query);

  /**
   * 가장 최근에 종료된 이동의 ID를 조회합니다.
   *
   * @param query 최근 종료된 이동 ID 조회 요청 쿼리
   * @return RouteIdPayload 최근의 종료 된 이동 ID 정보
   */
  RouteIdPayload readCurrentRouteId(CurrentRouteReadQuery query);

  /**
   * 경로 반경 내 사용자들의 ID 목록을 조회합니다.
   *
   * @param query 이동들의 경로 내 사용자 요청 쿼리
   * @return List<Integer> 분실물 경로 반경 내 사용자 ID 목록
   */
  List<Integer> findUserIdsNearPath(MembersInRouteQuery query);

  /**
   * 좌표 반경 내 사용자들의 ID 목록을 조회합니다.
   *
   * @param query 좌표 반경 내 사용자 요청 쿼리
   * @return List<Integer> 좌표 반경 내 사용자 ID 목록
   */
  List<Integer> findUserIdsNearPoint(MembersInPointQuery query);
}
