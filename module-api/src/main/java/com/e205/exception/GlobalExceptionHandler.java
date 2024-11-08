package com.e205.exception;

import com.e205.exception.dto.ErrorDetails;
import com.e205.exception.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(JsonProcessingException.class)
  public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
}