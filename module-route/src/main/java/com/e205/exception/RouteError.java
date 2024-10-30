package com.e205.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RouteError {

  NOT_FOUND_ROUTE("E201", "존재하지 않는 이동입니다."),
  ENDED_ROUTE("E202", "이미 종료된 이동입니다.");

  private String errorCode;
  private String message;
}
