package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.MemberWithFcm;
import com.e205.NotifiedMembersCommand;
import com.e205.NotifyOutboxEvent;
import com.e205.events.EventPublisher;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationProcessor {

  private final NotiCommandService notiCommandService;
  private final Notifier notifier;
  private final EventPublisher eventPublisher;

  public void notify(String fcm, String title, String body) {
    this.notifier.notify(fcm, title, body);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotifyOutboxEvent(NotifyOutboxEvent event) {
    this.notifier.notify(event.fcmToken(), event.notiType(), event.title());
  }

  @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
  @Transactional
  public List<NotifiedMembersCommand> processNotifications(Integer resourceId,
      String situationDescription, String eventType, List<MemberWithFcm> membersWithFcm) {
    List<NotifiedMembersCommand> notifiedMembers = new ArrayList<>();

    membersWithFcm.forEach(member -> {
      CreateNotificationCommand notification = createNotificationCommand(
          resourceId, situationDescription, eventType, member);
      this.notiCommandService.createNotification(notification);

      this.eventPublisher.publishAtLeastOnce(
          new NotifyOutboxEvent(member.fcmToken(), eventType, notification.getTitle()));

      notifiedMembers.add(new NotifiedMembersCommand(
          member.memberId(), resourceId, LocalDateTime.now(), eventType));
    });

    return notifiedMembers;
  }


  private CreateNotificationCommand createNotificationCommand(Integer resourceId,
      String situationDescription,
      String type, MemberWithFcm member) {
    String title = extractTitle(situationDescription);
    return CreateNotificationCommand.builder()
        .memberId(member.memberId())
        .resourceId(resourceId)
        .title(title)
        .createdAt(LocalDateTime.now())
        .type(type)
        .build();
  }

  private String extractTitle(String situationDescription) {
    return (situationDescription == null || situationDescription.isEmpty())
        ? "No description"
        : situationDescription.substring(0, Math.min(20, situationDescription.length()));
  }
}
