package com.e205.exception;

import static com.e205.exception.message.ErrorMessage.COMMA;
import static com.e205.exception.message.ErrorMessage.E777;
import static com.e205.exception.message.ErrorMessage.E999;
import static com.e205.exception.message.ErrorMessage.EQUALS;
import static com.e205.exception.message.ErrorMessage.EXCEPTION_HANDLED;
import static com.e205.exception.message.ErrorMessage.EXCEPTION_HANDLED_WITH_STACK;
import static com.e205.exception.message.ErrorMessage.TYPE_MISMATCH;

import com.e205.exception.dto.ErrorDetails;
import com.e205.exception.dto.ErrorResponse;
import com.e205.exception.dto.FieldError;
import com.e205.exception.dto.ValidationErrorDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorDetails> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    logRequestInfo(ex);
    List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldError(
            error.getField(),
            error.getDefaultMessage()))
        .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationErrorDetails(E777.toString(), errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ValidationErrorDetails> handleConstraintViolationException(ConstraintViolationException ex) {
    List<FieldError> errors = ex.getConstraintViolations().stream()
        .map(violation -> new FieldError(
            violation.getInvalidValue().toString(),
            violation.getMessage()))
        .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationErrorDetails(E777.toString(), errors));
  }

  @ExceptionHandler(TypeMismatchException.class)
  public ResponseEntity<ValidationErrorDetails> handleTypeMismatchException(
      TypeMismatchException ex) {
    String field = ex.getPropertyName();
    String message = String.format(TYPE_MISMATCH.toString(), ex.getValue());
    List<FieldError> errors = List.of(new FieldError(field, message));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationErrorDetails(E777.toString(), errors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> responseUnhandledExceptions(Exception ex) {
    ErrorDetails e999 = new ErrorDetails(E999.toString(), ex.getMessage(), 500);
    logRequestInfo(ex, true); // Log stack trace for internal errors
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.from(e999));
  }

  // Common log method to log request info and optionally stack trace
  private void logRequestInfo(Exception ex) {
    logRequestInfo(ex, false);
  }

  private void logRequestInfo(Exception ex, boolean includeStackTrace) {
    Map<String, String> mdcValues = MDC.getCopyOfContextMap();

    if (mdcValues != null && !mdcValues.isEmpty()) {
      String mdcInfo = mdcValues.entrySet()
          .stream()
          .map(entry -> entry.getKey() + EQUALS + entry.getValue())
          .collect(Collectors.joining(COMMA.toString()));

      if (includeStackTrace) {
        log.info(
            EXCEPTION_HANDLED.toString(),
            Thread.currentThread().getName(), mdcInfo, ex.getMessage(),
            ex.getStackTrace());
      } else {
        log.info(EXCEPTION_HANDLED_WITH_STACK.toString(),
            Thread.currentThread().getName(), mdcInfo, ex.getMessage()
        );
      }
    }
  }
}
