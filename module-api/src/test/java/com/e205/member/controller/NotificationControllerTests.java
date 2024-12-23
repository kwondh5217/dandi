package com.e205.member.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.e205.service.NotiCommandService;
import com.e205.service.NotiQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTests {

  @Mock
  NotiQueryService notiQueryService;
  @Mock
  NotiCommandService notiCommandService;
  @Mock
  RedisTemplate<String, Object> redisTemplate;

  @Test
  void test() {
    NotificationController notificationController = new NotificationController(notiQueryService, notiCommandService, redisTemplate);
  }

}