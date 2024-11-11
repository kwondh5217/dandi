package com.e205.byteBuddy;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@Component
@Aspect
public class RequestAspect {

  @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
      "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
      "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
      "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
      "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
      "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
  public void allRequestMappings() {}

  @Before("allRequestMappings()")
  public void beforeControllerMethod() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      Principal userPrincipal = request.getUserPrincipal();

      String requestId = UUID.randomUUID().toString();
      String userId = (userPrincipal != null) ? userPrincipal.getName() : "unknown";

      MDC.put("userId", userId);
      MDC.put("requestId", requestId);
      MDC.put("requestURI", request.getRequestURI());
    }
  }

  @After("allRequestMappings()")
  public void afterControllerMethod() {
    MDC.clear();
  }

  @After("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
  public void exceptionHandlerMethod() {
    MDC.clear();
  }

  @Around("@annotation(org.springframework.scheduling.annotation.Async)")
  public Object propagateMDC(ProceedingJoinPoint joinPoint) throws Throwable {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    try {
      if (contextMap != null) {
        MDC.setContextMap(contextMap);
      }
      return joinPoint.proceed();
    } finally {
      MDC.clear();
    }
  }
}