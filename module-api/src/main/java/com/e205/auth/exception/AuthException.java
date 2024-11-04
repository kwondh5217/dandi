package com.e205.auth.exception;

import com.e205.exception.ApplicationError;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {

  private final ApplicationError error;

  public AuthException(ApplicationError error) {
    super(error.getMessage());
    this.error = error;
  }
}
