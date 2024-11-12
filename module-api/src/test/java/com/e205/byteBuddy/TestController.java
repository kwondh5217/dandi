//package com.e205.byteBuddy;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Profile("test")
//@RequestMapping("/requestAspect")
//@RestController
//public class TestController {
//
//  private Logger log = LoggerFactory.getLogger(TestController.class);
//
//  @GetMapping
//  public ResponseEntity<?> test() {
//    String userId = MDC.get("userId");
//    String requestId = MDC.get("requestId");
//    String requestURI = MDC.get("requestURI");
//    log.info("Exception handled: currentThread={} userId={}, requestId={}, requestURI={}",
//        Thread.currentThread().getId(), userId, requestId, requestURI);
//    throw new IllegalArgumentException("test Exception");
//  }
//}
