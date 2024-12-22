package com.e205.service;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface Notifier {

  void notify(String deviceToken, String title, String body) throws Exception;

}
