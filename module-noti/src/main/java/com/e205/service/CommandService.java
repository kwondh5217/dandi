package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.entity.Notification;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommandService {

  private final NotificationRepository notificationRepository;

  public void createNotification(CreateNotificationCommand command) {
    Notification notification = NotificationFactory.createNotification(
        command.getType(), command.getResourceId());
    notification.setMemberId(command.getMemberId());
    notification.setTitle(command.getTitle());
    notification.setCreatedAt(command.getCreatedAt());

    this.notificationRepository.save(notification);
  }

}
