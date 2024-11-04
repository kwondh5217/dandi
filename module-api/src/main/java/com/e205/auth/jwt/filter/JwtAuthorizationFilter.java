package com.e205.auth.jwt.filter;

import static com.e205.exception.ApplicationError.EXAMPLE;

import com.e205.auth.dto.MemberDetails;
import com.e205.auth.exception.AuthException;
import com.e205.auth.jwt.JwtProvider;
import com.e205.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.e205.domain.member.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String token = request.getHeader("Authorization");

    if (isNotToken(token)) {
      return;
    }

    token = token.substring(7);
    if (!isValidatedToken(request, response, token)) {
      return;
    }

    MemberDetails memberDetails = new MemberDetails(createForAuthentication(token));
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        memberDetails, null,
        memberDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }

  private boolean isValidatedToken(HttpServletRequest request, HttpServletResponse response,
      String token
  ) throws IOException {
    if (!jwtProvider.verifyToken(token)) {
      // TODO <이현수> : 토큰 예외 메시지 전달 : 검증할 수 없는 토큰
      jwtAuthenticationEntryPoint.commence(request, response, new AuthException(EXAMPLE));
      return false;
    }

    if (!jwtProvider.isNotExpired(token)) {
      // TODO <이현수> : 토큰 예외 메시지 전달 : 만료된 토큰
      jwtAuthenticationEntryPoint.commence(request, response, new AuthException(EXAMPLE));
      return false;
    }
    return true;
  }

  private Member createForAuthentication(String token) {
    return Member.builder()
        .id(jwtProvider.getMemberId(token))
        .build();
  }

  private boolean isNotToken(String token) {
    return token == null || !token.startsWith("Bearer ");
  }
}
