package com.e205.auth.jwt.filter;

import static com.e205.exception.ApplicationError.EXAMPLE;

import com.e205.auth.dto.LoginRequest;
import com.e205.auth.dto.MemberDetails;
import com.e205.auth.exception.AuthException;
import com.e205.auth.jwt.JwtProvider;
import com.e205.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider provider;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    LoginRequest loginReq;

    try {
      loginReq = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
    } catch (IOException e) {
      throw new RuntimeException("Json 변환 오류");
    }

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(loginReq.email(), loginReq.password());

    return authenticationManager.authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication
  ) {
    MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
    Integer memberId = memberDetails.getId();

    String accessToken = provider.generateAccessToken(memberId);
    String refreshToken = provider.generateRefreshToken(memberId);

    // TODO <이현수> : refreshToken 저장

    response.addHeader("Authorization", "Bearer " + accessToken);
    response.addHeader("RefreshToken", refreshToken);
    response.setStatus(HttpStatus.OK.value());
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed ) throws IOException {
    // TODO <이현수> : 인증 실패 예외 처리
    jwtAuthenticationEntryPoint.commence(request, response, new AuthException(EXAMPLE) {
    });
  }
}
