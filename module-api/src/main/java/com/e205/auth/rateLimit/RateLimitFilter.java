package com.e205.auth.rateLimit;

import com.e205.auth.dto.MemberDetails;
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

  private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
  private final RateLimiterService rateLimiterService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (isPostRequest(request)) {
      String memberId = extractMemberId();

      if (!isRequestAllowed(memberId, request)) {
        logRateLimitExceeded(memberId, request);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean isPostRequest(HttpServletRequest request) {
    return HttpMethod.POST.name().equals(request.getMethod());
  }

  private String extractMemberId() {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      MemberDetails details = (MemberDetails) authentication.getPrincipal();
      return Integer.toString(details.id());
    }
    return "anonymous";
  }

  private boolean isRequestAllowed(String memberId, HttpServletRequest request) {
    HttpMethod method = HttpMethod.valueOf(request.getMethod());
    String requestUri = request.getRequestURI();
    return rateLimiterService.isRequestAllowed(memberId, method, requestUri);
  }

  private void logRateLimitExceeded(String memberId, HttpServletRequest request) {
    log.info("Request rejected by rate limiter - memberId: {}, method: {}, uri: {}",
        memberId, request.getMethod(), request.getRequestURI());
  }
}
