package com.e205.service;

import com.e205.MemberWithFcm;
import com.e205.query.MembersInRouteQuery;
import com.e205.NotifyEvent;
import com.e205.communication.MemberQueryService;
import com.e205.event.FoundItemSaveEvent;
import com.e205.event.LostItemSaveEvent;
import com.e205.event.RouteSavedEvent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
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

  @Retryable(backoff = @Backoff(delay = 500, multiplier = 1.5))
  public void handleRouteSavedEvent(RouteSavedEvent event) {
    String fcm = this.memberQueryService.findMemberFcmById(event.memberId());
    Assert.state(fcm != null, "No member FCM found");
    this.notificationProcessor.notify(fcm, event.getType(), event.payload());
  }

  public void handleNotifyEvent(NotifyEvent event) {
    String fcm = this.memberQueryService.findMemberFcmById(event.ownerId());
    Assert.state(fcm != null, "No member FCM found");
    this.notificationProcessor.notify(fcm, event.senderId() + " ", event.type());
  }

//  public void handleFoundItemSaveEvent(FoundItemSaveEvent event) {
//    handleItemSaveEvent(event, event.saved().endRouteId(),
//        event.saved().createdAt(), event.saved().id(),
//        event.saved().situationDescription(), event.getType());
//  }

  public void handleLostItemSaveEvent(LostItemSaveEvent event) {
    handleItemSaveEvent(event.saved().startRouteId(), event.saved().endRouteId(),
        event.saved().createdAt(), event.saved().id(),
        event.saved().situationDescription(), event.getType());
  }

  private void handleItemSaveEvent(Integer startRouteId, Integer endRouteId,
      LocalDateTime createdAt, Integer resourceId,
      String situationDescription, String eventType) {
    List<Integer> memberIds = findMemberIdsInRoute(startRouteId, endRouteId, createdAt);
    List<MemberWithFcm> membersWithFcm = this.memberQueryService.membersWithFcmQuery(
        memberIds);

    this.notificationProcessor.processNotifications(resourceId, situationDescription,
        eventType, membersWithFcm);
  }

  private List<Integer> findMemberIdsInRoute(Integer startRouteId, Integer endRouteId,
      LocalDateTime time) {
    LocalDateTime since = time.minusHours(this.notificationWindowHours);
    LocalDateTime until = time.plusHours(this.notificationWindowHours);
    // TODO <fosong98> RouteQueryService 충돌로 인한 null 처리
    return null;
//    return this.routeQueryService.findUserIdsNearPath(
//        new MembersInRouteQuery(startRouteId, endRouteId, since, until));
  }
}
