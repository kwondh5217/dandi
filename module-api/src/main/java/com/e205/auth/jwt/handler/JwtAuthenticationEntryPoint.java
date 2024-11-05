package com.e205.auth.jwt.handler;

import com.e205.auth.exception.AuthException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    setHeader(response);
    String responseData = mapper.writeValueAsString(buildResponseData(authException));
    response.getWriter().print(responseData);
  }

  private static void setHeader(HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  private Map<String, Object> buildResponseData(AuthenticationException exception) {
    if (exception instanceof AuthException authException) {
      return createCustomErrorResponse(authException);
    }
    return createDefaultErrorResponse();
  }

  private Map<String, Object> createCustomErrorResponse(AuthException authException) {
    Map<String, Object> responseData = new HashMap<>();
//    responseData.put("code", authException.getError().getCode());
    responseData.put("message", authException.getError().getMessage());
    return responseData;
  }

  private Map<String, Object> createDefaultErrorResponse() {
    Map<String, Object> responseData = new HashMap<>();
    responseData.put("message", "인증 실패");
    return responseData;
  }
}
