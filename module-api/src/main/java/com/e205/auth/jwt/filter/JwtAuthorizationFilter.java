package com.e205.auth.jwt.filter;

import static com.e205.auth.exception.AuthError.FAILED_VERIFY_TOKEN;
import static com.e205.auth.exception.AuthError.IS_EXPIRED_TOKEN;

import com.e205.auth.dto.MemberDetails;
import com.e205.auth.exception.AuthException;
import com.e205.auth.jwt.JwtProvider;
import com.e205.auth.jwt.handler.JwtAuthenticationEntryPoint;
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

  private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String token = request.getHeader("Authorization");

    if (isNotToken(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    token = token.substring(7);
    if (!isValidateIfNotCallCommence(request, response, token)) {
      return;
    }

    MemberDetails memberDetails = new MemberDetails(jwtProvider.getMemberId(token));
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        memberDetails, null,
        memberDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  private boolean isValidateIfNotCallCommence(HttpServletRequest request,
      HttpServletResponse response,
      String token
  ) throws IOException {
    if (!jwtProvider.verifyToken(token)) {
      jwtAuthEntryPoint.commence(request, response, new AuthException(FAILED_VERIFY_TOKEN));
      return false;
    }

    if (jwtProvider.isExpired(token)) {
      jwtAuthEntryPoint.commence(request, response, new AuthException(IS_EXPIRED_TOKEN));
      return false;
    }
    return true;
  }

  private boolean isNotToken(String token) {
    return token == null || !token.startsWith("Bearer ");
  }
}
