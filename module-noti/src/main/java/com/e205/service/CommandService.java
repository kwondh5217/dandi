package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.DeleteNotificationsCommand;
import com.e205.NotifiedMembersCommand;
import com.e205.command.ConfirmItemCommand;
import com.e205.communication.ItemCommandService;
import com.e205.entity.Notification;
import com.e205.repository.FoundItemNotificationRepository;
import com.e205.repository.LostItemNotificationRepository;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CommandService {

  private final NotificationRepository notificationRepository;
  private final ItemCommandService itemCommandService;
  private final LostItemNotificationRepository lostItemNotificationRepository;
  private final FoundItemNotificationRepository foundItemNotificationRepository;

  public void createNotification(CreateNotificationCommand command) {
    Notification notification = NotificationFactory.createNotification(
        command.getType(), command.getResourceId());
    notification.setMemberId(command.getMemberId());
    notification.setTitle(command.getTitle());
    notification.setCreatedAt(command.getCreatedAt());

    notificationRepository.save(notification);
  }

  public void deleteNotifications(DeleteNotificationsCommand command) {
    notificationRepository.deleteAllByIdInBatch(command.notificationIds());
  }

  public void notifiedMembersCommand(List<NotifiedMembersCommand> command) {
    itemCommandService.saveNotifiedMembers(command);
  }

  public void confirmItemNotification(ConfirmItemCommand command) {
    Notification notification = findNotificationByTypeAndId(command.type(), command.itemId());
    notification.confirmRead();
  }

  private Notification findNotificationByTypeAndId(String type, Integer itemId) {
    return switch (type) {
      case "lostItem" -> lostItemNotificationRepository.findById(itemId)
          .orElseThrow(() -> new RuntimeException("존재하지 않는 알림입니다."));
      case "foundItem" -> foundItemNotificationRepository.findById(itemId)
          .orElseThrow(() -> new RuntimeException("존재하지 않는 알림입니다."));
      default -> throw new IllegalArgumentException("지원하지 않는 알림 유형입니다.");
    };
  }
}
