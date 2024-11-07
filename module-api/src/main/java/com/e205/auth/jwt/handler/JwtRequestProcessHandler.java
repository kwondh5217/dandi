package com.e205.auth.jwt.handler;

import com.e205.auth.jwt.JwtProvider;
import com.e205.auth.jwt.repository.JwtRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class JwtRequestProcessHandler {

  private static final String TOKEN_TYPE = "Bearer ";
  private static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";
  private static final String REFRESH_TOKEN_HEADER_NAME = "RefreshToken";

  private final JwtRepository jwtRepository;
  private final JwtProvider jwtProvider;

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/auth/refresh")
  public void issueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
    String headerValue = request.getHeader(REFRESH_TOKEN_HEADER_NAME);
    String token = extractRefreshToken(headerValue);
    validateRefreshToken(token);

    Integer memberId = jwtProvider.getMemberId(token);
    validateRefreshTokenInRedis(memberId);
    jwtRepository.deleteRefreshTokenByMemberId(memberId);

    String newAccessToken = jwtProvider.generateAccessToken(memberId);
    String newRefreshToken = jwtProvider.generateRefreshToken(memberId);
    jwtRepository.saveRefreshToken(memberId, newRefreshToken);

    response.setHeader(ACCESS_TOKEN_HEADER_NAME, TOKEN_TYPE + newAccessToken);
    response.setHeader(REFRESH_TOKEN_HEADER_NAME, TOKEN_TYPE + newRefreshToken);
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/auth/logout")
  public void logout(HttpServletRequest request) {
    String headerValue = request.getHeader(REFRESH_TOKEN_HEADER_NAME);
    String refreshToken = extractRefreshToken(headerValue);

    validateRefreshToken(refreshToken);

    Integer memberId = jwtProvider.getMemberId(refreshToken);
    validateRefreshTokenInRedis(memberId);

    jwtRepository.deleteRefreshTokenByMemberId(memberId);
  }

  private String extractRefreshToken(String headerValue) {
    if (headerValue == null || !headerValue.startsWith(TOKEN_TYPE)) {
      throw new RuntimeException("재발급 토큰이 존재하지 않습니다.");
    }

    return headerValue.substring(7);
  }

  private void validateRefreshToken(String refreshToken) {
    if (!jwtProvider.verifyToken(refreshToken)) {
      throw new RuntimeException("유효하지 않은 재발급 토큰입니다.");
    }

    if (jwtProvider.isExpired(refreshToken)) {
      throw new RuntimeException("만료된 재발급 토큰입니다.");
    }
  }

  private void validateRefreshTokenInRedis(Integer memberId) {
    if (!jwtRepository.existByMemberId(memberId)) {
      throw new RuntimeException("사용할 수 없는 재발급 토큰입니다.");
    }
  }
}
