package com.e205.service;

import com.e205.CommentSaveCommand;
import com.e205.ConfirmItemCommand;
import com.e205.CreateNotificationCommand;
import com.e205.DeleteNotificationsCommand;
import com.e205.ItemCommandService;
import com.e205.NotifiedMembersCommand;
import com.e205.NotifyOutboxEvent;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.query.FindMembersByIdQuery;
import com.e205.command.member.service.MemberQueryService;
import com.e205.entity.CommentNotification;
import com.e205.entity.Notification;
import com.e205.events.EventPublisher;
import com.e205.repository.CommentNotificationRepository;
import com.e205.repository.FoundItemNotificationRepository;
import com.e205.repository.LostItemNotificationRepository;
import com.e205.repository.NotificationRepository;
import com.e205.util.NotificationFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotiCommandService implements com.e205.NotiCommandService {

  private final NotificationRepository notificationRepository;
  private final ItemCommandService itemCommandService;
  private final LostItemNotificationRepository lostItemNotificationRepository;
  private final FoundItemNotificationRepository foundItemNotificationRepository;
  private final CommentNotificationRepository commentNotificationRepository;
  private final EventPublisher eventPublisher;
  private final MemberQueryService memberQueryService;

  public void createNotification(CreateNotificationCommand command) {
    Notification notification = NotificationFactory.createNotification(
        command.getNotiType(), command.getResourceId());
    notification.setMemberId(command.getMemberId());
    notification.setTitle(command.getTitle());
    notification.setCreatedAt(command.getCreatedAt());

    notificationRepository.save(notification);
  }

  public void deleteNotifications(DeleteNotificationsCommand command) {
    notificationRepository.deleteAllByIdAndMemberId(command.memberId(), command.notificationIds());
  }

  public void notifiedMembersCommand(List<NotifiedMembersCommand> command) {
    itemCommandService.saveNotifiedMembers(command);
  }

  public void confirmItemNotification(ConfirmItemCommand command) {
    findNotificationByTypeAndId(command.type(), command.itemId())
        .forEach(Notification::confirmRead);
  }

  public void createCommentNotification(CommentSaveCommand command) {
    List<MemberPayload> members = memberQueryService.findMembers(
        new FindMembersByIdQuery(new ArrayList<>(command.senders())));

    for (MemberPayload member : members) {
      Notification notification = NotificationFactory.createNotification(command.type(),
          command.commentId());
      notification.setMemberId(member.id());
      notification.setTitle(command.type());
      notification.setCreatedAt(LocalDateTime.now());

      commentNotificationRepository.save((CommentNotification) notification);

      if (member.commentAlarm()) {
        eventPublisher.publishAtLeastOnce(
            new NotifyOutboxEvent(member.fcmCode(), command.type(), notification.getBody()));
      }
    }
  }

  private List<Notification> findNotificationByTypeAndId(String type, Integer itemId) {
    return switch (NotificationType.fromString(type)) {
      case LOST_ITEM -> lostItemNotificationRepository.findByLostItemId(itemId);
      case FOUND_ITEM -> foundItemNotificationRepository.findByFoundItemId(itemId);
      default -> throw new IllegalArgumentException("지원하지 않는 알림 유형입니다.");
    };
  }
}
