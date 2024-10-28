package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.annotation.CommandMethod;
import com.e205.commands.Command;
import com.e205.entity.Notification;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommandService {

  private final NotificationRepository notificationRepository;

  @CommandMethod(type = "createNotification")
  public void createNotification(Command command) {
    if (!(command instanceof CreateNotificationCommand createNotificationCommand)) {
      throw new IllegalArgumentException("Invalid command type");
    }

    Notification notification = NotificationFactory.createNotification(
        createNotificationCommand.getType(), createNotificationCommand.getResourceId());
    notification.setMemberId(createNotificationCommand.getMemberId());
    notification.setTitle(createNotificationCommand.getTitle());
    notification.setCreatedAt(createNotificationCommand.getCreatedAt());

    this.notificationRepository.save(notification);
  }

}
