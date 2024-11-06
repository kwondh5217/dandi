package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.MemberWithFcm;
import com.e205.NotifiedMembersCommand;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationProcessor {

  protected static final int CHUNK_SIZE = 100;

  private final NotiCommandService notiCommandService;
  private final RetryTemplate retryTemplate;
  private final Notifier notifier;
  private final PlatformTransactionManager transactionManager;

  public void notify(String fcm, String title, String body) {
    this.notifier.notify(fcm, title, body);
  }

  public List<NotifiedMembersCommand> processNotifications(Integer resourceId,
      String situationDescription,
      String eventType, List<MemberWithFcm> membersWithFcm) {
    List<NotifiedMembersCommand> notifiedMembers = new ArrayList<>();
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    for (int i = 0; i < membersWithFcm.size(); i += CHUNK_SIZE) {
      List<MemberWithFcm> chunk = membersWithFcm.subList(i,
          Math.min(i + CHUNK_SIZE, membersWithFcm.size()));
      TransactionStatus status = transactionManager.getTransaction(def);

      try {
        processNotificationChunk(chunk, resourceId, situationDescription, eventType,
            notifiedMembers);
        transactionManager.commit(status);
      } catch (Exception e) {
        log.warn("Error in chunk, rolling back chunk transaction: " + e.getMessage());
        transactionManager.rollback(status);
        throw e;
      }
    }
    return notifiedMembers;
  }

  private void processNotificationChunk(List<MemberWithFcm> chunk, Integer resourceId,
      String situationDescription, String eventType,
      List<NotifiedMembersCommand> notifiedMembers) {
    chunk.forEach(member -> {
      CreateNotificationCommand notification = createNotificationCommand(resourceId,
          situationDescription, eventType, member);
      notiCommandService.createNotification(notification);

      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              retryTemplate.execute(context -> {
                notifier.notify(member.fcmToken(), eventType, notification.getTitle());
                return null;
              });
            }
          });

      notifiedMembers.add(
          new NotifiedMembersCommand(member.memberId(), LocalDateTime.now(), eventType));
    });
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
