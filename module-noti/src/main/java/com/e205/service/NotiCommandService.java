package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.DeleteNotificationsCommand;
import com.e205.ItemCommandService;
import com.e205.NotifiedMembersCommand;
import com.e205.command.ConfirmItemCommand;
import com.e205.entity.Notification;
import com.e205.repository.FoundItemNotificationRepository;
import com.e205.repository.LostItemNotificationRepository;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotiCommandService {

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
    notificationRepository.deleteAllByIdAndMemberId(command.memberId(),
        command.notificationIds());
  }

  public void notifiedMembersCommand(List<NotifiedMembersCommand> command) {
    itemCommandService.saveNotifiedMembers(command);
  }

  public void confirmItemNotification(ConfirmItemCommand command) {
    findNotificationByTypeAndId(command.type(), command.itemId())
        .forEach(Notification::confirmRead);
  }

  private List<Notification> findNotificationByTypeAndId(String type, Integer itemId) {
    return switch (type) {
      case "lostItem" -> lostItemNotificationRepository.findByLostItemId(itemId);
      case "foundItem" -> foundItemNotificationRepository.findByFoundItemId(itemId);
      default -> throw new IllegalArgumentException("지원하지 않는 알림 유형입니다.");
    };
  }
}
