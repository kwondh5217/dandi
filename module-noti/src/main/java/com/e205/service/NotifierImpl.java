package com.e205.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifierImpl implements Notifier {

  @Async("fcmPushTaskExecutor")
  @Override
  public void notify(String deviceToken, String title, String body) {
    Message message = Message.builder()
        .setToken(deviceToken)
        .setNotification(
            Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
        .build();

    try {
      FirebaseMessaging instance = FirebaseMessaging.getInstance();
      instance.send(message);
    } catch (FirebaseMessagingException e) {
      log.warn("Failed to send notification to deviceToken: {} with title: {}. Reason: {}",
          deviceToken, title, e.getMessage());
      throw new RuntimeException(e);
    }

  }
}
