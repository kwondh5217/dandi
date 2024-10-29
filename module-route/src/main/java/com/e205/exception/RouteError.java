package com.e205.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RouteError {

  NOT_FOUNT_ROUTE("E201", "존재하지 않는 이동입니다.");

  private String errorCode;
  private String message;
}
