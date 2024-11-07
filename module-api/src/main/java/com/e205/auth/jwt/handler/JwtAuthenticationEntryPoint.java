package com.e205.auth.jwt.handler;

import com.e205.auth.exception.AuthError;
import com.e205.auth.exception.AuthException;
import com.e205.exception.ExceptionLoader;
import com.e205.exception.dto.ErrorDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    setHeader(response);
    String responseData = mapper.writeValueAsString(setBody(authException));
    response.getWriter().print(responseData);
  }

  private static void setHeader(HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  private Map<String, Object> setBody(AuthenticationException exception) {
    if (exception instanceof AuthException authException) {
      return createCustomErrorResponse(authException);
    }
    return createDefaultErrorResponse();
  }

  private Map<String, Object> createCustomErrorResponse(AuthException authException) {
    AuthError error = authException.getError();
    return Map.of(
        "code", error.getCode(),
        "message", error.getMessage()
    );
  }

  private Map<String, Object> createDefaultErrorResponse() {
    AuthError error = AuthError.FAILED_AUTHENTICATION;
    return Map.of(
        "code", error.getCode(),
        "message", error.getMessage()
    );
  }
}
