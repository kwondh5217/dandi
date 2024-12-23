package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.NotifiedMembersCommand;
import com.e205.NotifyOutboxEvent;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.util.NotificationFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

  public static final String FOUND_ITEM_SAVE_EVENT = "foundItem";
  public static final String LOST_ITEM_SAVE_EVENT = "lostItem";
  private final NotiCommandService notiCommandService;
  private final Notifier notifier;
  private final ApplicationEventPublisher eventPublisher;

  public void notify(final String fcm, final String title, final String body) {
    try {
      this.notifier.notify(fcm, title, body);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotifyOutboxEvent(NotifyOutboxEvent event) {
    try {
      this.notifier.notify(event.fcmToken(), event.notiType(), event.title());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
  @Transactional
  public List<NotifiedMembersCommand> processNotifications(
      final Integer resourceId, final Integer senderId, String situationDescription,
      final String eventType, final List<MemberPayload> memberPayloads) {
    List<NotifiedMembersCommand> notifiedMembers = new ArrayList<>();
    String eventBody = NotificationFactory.createNotificationBody(eventType, resourceId);

    memberPayloads.forEach(member -> {
      if(!senderId.equals(member.id())) {
        CreateNotificationCommand notification = createNotificationCommand(
            resourceId, situationDescription, eventType, member, eventBody);
        this.notiCommandService.createNotification(notification);

        if (shouldNotify(eventType, member)) {
          this.eventPublisher.publishEvent(new NotifyOutboxEvent(
              member.fcmCode(), notification.getTitle(), eventBody)
          );
        }

        notifiedMembers.add(new NotifiedMembersCommand(
            member.id(), resourceId, LocalDateTime.now(), eventType)
        );
      }
    });

    return notifiedMembers;
  }

  private CreateNotificationCommand createNotificationCommand(final Integer resourceId,
      final String situationDescription,
      final String type, MemberPayload member, final String body) {
    String title = extractTitle(situationDescription);
    return CreateNotificationCommand.builder()
        .memberId(member.id())
        .resourceId(resourceId)
        .title(title)
        .createdAt(LocalDateTime.now())
        .type(type)
        .body(body)
        .build();
  }

  private String extractTitle(final String situationDescription) {
    return (situationDescription == null || situationDescription.isEmpty())
        ? "No description"
        : situationDescription.substring(0, Math.min(20, situationDescription.length()));
  }

  private boolean shouldNotify(final String eventType, MemberPayload member) {
    return (eventType.equals(FOUND_ITEM_SAVE_EVENT) && member.foundItemAlarm()) ||
        (eventType.equals(LOST_ITEM_SAVE_EVENT) && member.lostItemAlarm());
  }
}
