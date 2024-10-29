package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.DeleteNotificationsCommand;
import com.e205.NotifiedMembersCommand;
import com.e205.communication.ItemCommandService;
import com.e205.entity.Notification;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommandService {

  private final NotificationRepository notificationRepository;
  private final ItemCommandService itemCommandService;

  public void createNotification(CreateNotificationCommand command) {
    Notification notification = NotificationFactory.createNotification(
        command.getType(), command.getResourceId());
    notification.setMemberId(command.getMemberId());
    notification.setTitle(command.getTitle());
    notification.setCreatedAt(command.getCreatedAt());

    this.notificationRepository.save(notification);
  }

  public void deleteNotifications(DeleteNotificationsCommand command) {
    this.notificationRepository.deleteAllByIdInBatch(command.notificationIds());
  }

  public void notifiedMembersCommand(List<NotifiedMembersCommand> command) {
    this.itemCommandService.saveNotifiedMembers(command);
  }

}
