package com.e205.service;

import com.e205.NotifyEvent;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.query.FindMembersByIdQuery;
import com.e205.command.member.service.MemberQueryService;
import com.e205.event.FoundItemSaveEvent;
import com.e205.event.LostItemSaveEvent;
import com.e205.event.RouteSavedEvent;
import com.e205.query.MembersInPointQuery;
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
  private final NotiCommandService notiCommandService;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRouteSavedEvent(RouteSavedEvent event) {
    final String fcm = this.memberQueryService.findMemberFcmById(event.memberId());
    Assert.state(fcm != null, "fcm must not be null");
    this.notificationProcessor.notify(fcm, event.getType(), event.payload());
  }

  @EventListener
  public void handleNotifyEvent(NotifyEvent event) {
    final String fcm = this.memberQueryService.findMemberFcmById(event.ownerId());
    Assert.state(fcm != null, "fcm must not be null");
    this.notificationProcessor.notify(fcm, event.senderId() + " ", event.type());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleFoundItemSaveEvent(FoundItemSaveEvent event) {
    final List<Integer> userIdsNearPoint = this.routeQueryService.findUserIdsNearPoint(
        new MembersInPointQuery(
            event.saved().lat(),
            event.saved().lon(),
            notificationWindowHours));

    processNotificationForMembers(
        event.saved().id(),
        event.saved().memberId(),
        event.saved().description(),
        event.getType(),
        this.memberQueryService.findMembers(new FindMembersByIdQuery(userIdsNearPoint)));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLostItemSaveEvent(LostItemSaveEvent event) {
    final List<MemberPayload> memberPayloads = findMembersForNotification(
        event.saved().startRouteId(),
        event.saved().endRouteId(),
        event.saved().createdAt());

    processNotificationForMembers(
        event.saved().id(),
        event.saved().memberId(),
        event.saved().situationDescription(),
        event.getType(),
        memberPayloads);
  }

  private List<MemberPayload> findMembersForNotification(final Integer startRouteId,
      final Integer endRouteId, final LocalDateTime createdAt) {
    final List<Integer> memberIds = findMemberIdsInRoute(startRouteId, endRouteId,
        createdAt);
    return this.memberQueryService.findMembers(new FindMembersByIdQuery(memberIds));
  }

  private List<Integer> findMemberIdsInRoute(final Integer startRouteId,
      final Integer endRouteId, final LocalDateTime time) {
    final LocalDateTime since = time.minusHours(notificationWindowHours);
    return this.routeQueryService.findUserIdsNearPath(
        new MembersInRouteQuery(startRouteId, endRouteId, since));
  }

  private void processNotificationForMembers(final Integer resourceId,
      final Integer senderId, final String situationDescription, final String eventType,
      final List<MemberPayload> memberPayloads) {
    Assert.notNull(senderId, "senderId must not be null");
    this.notiCommandService.notifiedMembersCommand(
        this.notificationProcessor.processNotifications(
            resourceId, senderId, situationDescription, eventType,
            memberPayloads)
    );
  }
}
