package com.e205.exception;

import com.e205.exception.dto.ErrorDetails;
import com.e205.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

  private final ExceptionLoader errorCodeManager;

  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex) {
    String errorCode = ex.getCode();
    ErrorDetails details = errorCodeManager.getErrorDetails(errorCode);
    return ResponseEntity.status(details.httpStatus()).body(ErrorResponse.from(details));
  }
}