package com.e205.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotifierImpl implements Notifier {

  @Override
  public void notify(String deviceToken) {
    Message message = Message.builder()
        .setToken(deviceToken)
        .setNotification(
            Notification.builder()
                .setTitle("title")
                .setBody("body")
                .build())
        .build();

    try {
      FirebaseMessaging instance = FirebaseMessaging.getInstance();
      instance.send(message);
    } catch (FirebaseMessagingException e) {
      throw new RuntimeException(e);
    }

  }
}
