package com.e205.service;

import com.e205.CreateNotificationCommand;
import com.e205.MemberWithFcm;
import com.e205.MembersInRouteQuery;
import com.e205.NotifiedMembersCommand;
import com.e205.communication.ItemCommandService;
import com.e205.communication.MemberQueryService;
import com.e205.communication.RouteQueryService;
import com.e205.event.LostItemSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

  private static final int CHUNK_SIZE = 100;

  @Value("${noti.until.time}")
  private int notificationWindowHours;

  private final RouteQueryService routeQueryService;
  private final Notifier notifier;
  private final MemberQueryService memberQueryService;
  private final CommandService commandService;
  private final PlatformTransactionManager transactionManager;
  private final RetryTemplate retryTemplate;
  private final ItemCommandService itemCommandService;

  public void handleLostItemSaveEvent(LostItemSaveEvent event) {
    LocalDateTime eventTime = event.saved().createdAt();
    LocalDateTime since = eventTime.minusHours(this.notificationWindowHours);
    LocalDateTime until = eventTime.plusHours(this.notificationWindowHours);

    List<Integer> memberIds = this.routeQueryService.queryMembersInPoints(
        new MembersInRouteQuery(event.saved().route(), since, until)
    );

    List<NotifiedMembersCommand> notifiedMembers = this.notifyMembersAndCreateCommands(event, memberIds);

    this.itemCommandService.saveNotifiedMembers(notifiedMembers);
  }

  private List<NotifiedMembersCommand> notifyMembersAndCreateCommands(LostItemSaveEvent event,
      List<Integer> memberIds) {
    List<MemberWithFcm> membersWithFcm = this.memberQueryService.memberWithFcmQuery(memberIds);
    List<NotifiedMembersCommand> notifiedMembers = new ArrayList<>();

    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    for (int i = 0; i < membersWithFcm.size(); i += CHUNK_SIZE) {
      List<MemberWithFcm> chunk = membersWithFcm.subList(i, Math.min(i + CHUNK_SIZE, membersWithFcm.size()));
      TransactionStatus status = this.transactionManager.getTransaction(def);

      try {
        chunk.forEach(member -> {
          this.commandService.createNotification(createNotificationCommand(event, member));

          TransactionSynchronizationManager.registerSynchronization(
              new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                  retryTemplate.execute(context -> {
                    notifier.notify(member.fcmToken());
                    return null;
                  });
                }
              }
          );

          notifiedMembers.add(new NotifiedMembersCommand(member.memberId(), LocalDateTime.now()));
        });

        this.transactionManager.commit(status);

      } catch (Exception e) {
        log.warn("Error in chunk, rolling back chunk transaction: " + e.getMessage());
        this.transactionManager.rollback(status);
        throw e;
      }
    }

    return notifiedMembers;
  }

  private CreateNotificationCommand createNotificationCommand(LostItemSaveEvent event, MemberWithFcm member) {
    String title = extractTitle(event);

    return CreateNotificationCommand.builder()
        .memberId(member.memberId())
        .resourceId(event.saved().id())
        .title(title)
        .createdAt(LocalDateTime.now())
        .type("lostItem")
        .build();
  }

  private String extractTitle(LostItemSaveEvent event) {
    String situationDescription = event.saved().situationDescription();
    if (situationDescription == null) {
      return "No description";
    }
    return situationDescription.length() >= 20
        ? situationDescription.substring(0, 20)
        : situationDescription;
  }
}
