package com.e205.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationError {
  EXAMPLE();
  private String message;
}
