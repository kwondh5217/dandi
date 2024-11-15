package com.e205.auth.rateLimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

  private Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
  private final RateLimiterService rateLimiterService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String memberId = (authentication == null || !authentication.isAuthenticated())
        ? "anonymous" : authentication.getName();

    if (!rateLimiterService.isRequestAllowed(
        memberId, HttpMethod.valueOf(request.getMethod()), request.getRequestURI())) {
      log.info("Request rejected by rate limiter, memberId:{}, method:{}, uri:{}",
          memberId, request.getMethod(), request.getRequestURI());
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      return;
    }

    filterChain.doFilter(request, response);
  }
}
