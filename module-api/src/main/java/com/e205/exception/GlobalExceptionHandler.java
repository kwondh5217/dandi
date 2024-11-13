package com.e205.exception;

import com.e205.exception.dto.ErrorDetails;
import com.e205.exception.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

  private final ExceptionLoader errorCodeManager;

  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException ex) {
    logRequestInfo(ex);
    String errorCode = ex.getCode();
    ErrorDetails details = errorCodeManager.getErrorDetails(errorCode);
    return ResponseEntity.status(details.httpStatus()).body(ErrorResponse.from(details));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
    logRequestInfo(ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    logRequestInfo(ex);
    log.info(Arrays.toString(ex.getStackTrace()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(JsonProcessingException.class)
  public ResponseEntity<String> handleJsonProcessingException(
      JsonProcessingException ex) {
    logRequestInfo(ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> responseUnhandledExceptions(Exception ex) {
    ErrorDetails e999 = new ErrorDetails("E999", ex.getMessage(), 500);
    logRequestInfo(ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.from(e999));
  }

  private void logRequestInfo(Exception ex) {
    Map<String, String> mdcValues = MDC.getCopyOfContextMap();

    if(mdcValues != null && !mdcValues.isEmpty()) {
      String mdcInfo = mdcValues.entrySet()
          .stream()
          .map(entry -> entry.getKey() + "=" + entry.getValue())
          .collect(Collectors.joining(", "));

      log.info("Exception handled: currentThread={}, {}, exceptionMessage={}, stackTrace={}",
          Thread.currentThread().getName(), mdcInfo, ex.getMessage(), ex.getStackTrace());
    }
  }
}