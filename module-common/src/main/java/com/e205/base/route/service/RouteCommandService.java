package com.e205.base.route.service;

import com.e205.base.route.command.RouteCreateCommand;
import com.e205.base.route.command.RouteEndCommand;
import com.e205.base.route.command.SnapshotUpdateCommand;

public interface RouteCommandService {

  /**
   * 새로운 이동를 생성합니다.
   *
   * @param command 이동 생성 명령
   */
  void createRoute(RouteCreateCommand command);

  /**
   * 이동의 스냅샷을 업데이트합니다.
   *
   * @param command 스냅샷 업데이트 명령
   */
  void updateSnapshot(SnapshotUpdateCommand command);

  /**
   * 이동를 종료합니다.
   *
   * @param command 이동 종료 명령
   */
  void endRoute(RouteEndCommand command);
}
