package com.e205.exception.dto;

public record ErrorResponse(
    String code,
    String message
) {

  public static ErrorResponse from(ErrorDetails error) {
    return new ErrorResponse(error.code(), error.message());
  }
}
