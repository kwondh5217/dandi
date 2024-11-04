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
      AuthenticationException authException
  ) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    AuthException e = (AuthException) authException;

    setHeader(response);
    response.getWriter().print(mapper.writeValueAsString(setBody(e)));
  }

  private static void setHeader(HttpServletResponse response) {
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  private static Map<String, Object> setBody(AuthException e) {
    Map<String, Object> responseData = new HashMap<>();
    responseData.put("message", "예외");
    // responseData.put("message", e.getError().getMessage());
    // TODO <이현수> : 지정 예외 코드 포함
    return responseData;
  }
}
