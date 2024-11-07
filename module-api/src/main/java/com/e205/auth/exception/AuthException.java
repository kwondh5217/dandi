package com.e205.auth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {

  private final AuthError error;

  public AuthException(AuthError error) {
    super(error.getMessage());
    this.error = error;
  }
}
