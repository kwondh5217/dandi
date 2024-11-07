package com.e205.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalException extends RuntimeException {

  private final String code;

  public GlobalException(Throwable cause, String code) {
    super(cause);
    this.code = code;
  }
}
