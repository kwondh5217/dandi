package com.e205.service;

import com.e205.MemberWithFcm;
import com.e205.NotifyEvent;
import com.e205.communication.MemberQueryService;
import com.e205.event.LostItemSaveEvent;
import com.e205.event.RouteSavedEvent;
import com.e205.query.MembersInRouteQuery;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

  @Value("${noti.until.time}")
  private int notificationWindowHours;

  private final RouteQueryService routeQueryService;
  private final NotificationProcessor notificationProcessor;
  private final MemberQueryService memberQueryService;
  private final CommandService commandService;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRouteSavedEvent(RouteSavedEvent event) {
    String fcm = this.memberQueryService.findMemberFcmById(event.memberId());
    Assert.state(fcm != null, "fcm must not be null");
    this.notificationProcessor.notify(fcm, event.getType(), event.payload());
  }

  @EventListener
  public void handleNotifyEvent(NotifyEvent event) {
    String fcm = this.memberQueryService.findMemberFcmById(event.ownerId());
    Assert.state(fcm != null, "fcm must not be null");
    this.notificationProcessor.notify(fcm, event.senderId() + " ", event.type());
  }

//  public void handleFoundItemSaveEvent(FoundItemSaveEvent event) {
//    handleItemSaveEvent(event, event.saved().endRouteId(),
//        event.saved().createdAt(), event.saved().id(),
//        event.saved().situationDescription(), event.getType());
//  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLostItemSaveEvent(LostItemSaveEvent event) {
    List<MemberWithFcm> membersWithFcm = findMembersForNotification(
        event.saved().startRouteId(),
        event.saved().endRouteId(),
        event.saved().createdAt());

    processNotificationForMembers(
        event.saved().id(),
        event.saved().situationDescription(),
        event.getType(),
        membersWithFcm);
  }

  private List<MemberWithFcm> findMembersForNotification(Integer startRouteId,
      Integer endRouteId, LocalDateTime createdAt) {
    List<Integer> memberIds = findMemberIdsInRoute(startRouteId, endRouteId, createdAt);
    return this.memberQueryService.membersWithFcmQuery(memberIds);
  }

  private List<Integer> findMemberIdsInRoute(Integer startRouteId,
      Integer endRouteId, LocalDateTime time) {
    LocalDateTime since = time.minusHours(notificationWindowHours);
    return this.routeQueryService.findUserIdsNearPath(
        new MembersInRouteQuery(startRouteId, endRouteId, since));
  }

  private void processNotificationForMembers(Integer resourceId,
      String situationDescription, String eventType, List<MemberWithFcm> membersWithFcm) {
    this.commandService.notifiedMembersCommand(
        this.notificationProcessor.processNotifications(
            resourceId, situationDescription, eventType,
            membersWithFcm
        ));
  }
}
