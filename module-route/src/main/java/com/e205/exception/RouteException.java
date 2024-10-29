package com.e205.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RouteException extends RuntimeException {

  private RouteError error;

  public RouteException(Throwable cause, RouteError error) {
    super(cause);
    this.error = error;
  }
}
