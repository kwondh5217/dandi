package com.e205.byteBuddy;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
  public void allRequestMappings() {
  }

  @Pointcut("execution(* com.e205..*.*(..))")
  public void allComE205Exceptions() {
  }

  @Before("allRequestMappings()")
  public void beforeControllerMethod() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      MDC.clear();

      HttpServletRequest request = attributes.getRequest();
      Principal userPrincipal = request.getUserPrincipal();

      String requestId = UUID.randomUUID().toString();
      String userId = (userPrincipal != null) ? userPrincipal.getName() : "unknown";

      MDC.put("userId", userId);
      MDC.put("requestId", requestId);
      MDC.put("requestURI", request.getRequestURI());
    }
  }

  @AfterThrowing(pointcut = "allComE205Exceptions()", throwing = "ex")
  public void logException(Exception ex) throws Exception {
    StackTraceElement stackTraceElement = ex.getStackTrace()[0];
    String className = stackTraceElement.getClassName();
    int lineNumber = stackTraceElement.getLineNumber();

    MDC.put("exceptionClassName", className);
    MDC.put("exceptionLineNumber", String.valueOf(lineNumber));

    throw ex;
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