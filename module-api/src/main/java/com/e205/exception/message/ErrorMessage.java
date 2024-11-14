package com.e205.exception.message;

public enum ErrorMessage {
  E999("E999"),
  E777("E777"),
  INTERNAL_SERVER_ERROR("Internal server error"),
  VALIDATION_FAILED("Validation failed"),
  ILLEGAL_ARGUMENT("Invalid argument provided"),
  JSON_PROCESSING_ERROR("JSON processing error"),
  CONSTRAINT_VIOLATION("Constraint violation"),
  TYPE_MISMATCH("올바른 타입이 아닙니다. 입력값: '%s'"),
  EXCEPTION_HANDLED("Exception handled: currentThread={}, {}, exceptionMessage={}"),
  EXCEPTION_HANDLED_WITH_STACK(
      "Exception handled: currentThread={}, {}, exceptionMessage={}, stackTrace={}"
  ),

  EQUALS("="),
  COMMA(", ");

  private final String message;

  ErrorMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return message;
  }
}
