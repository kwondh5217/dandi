package com.e205.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.command.member.service.MemberQueryService;
import com.e205.event.LostItemSaveEvent;
import com.e205.event.RouteSavedEvent;
import com.e205.payload.LostItemPayload;
import com.e205.query.MembersInRouteQuery;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventServiceTest {

  private RouteQueryService routeQueryService;
  private MemberQueryService memberQueryService;
  private NotificationProcessor notifier;
  private EventService eventService;
  private NotiCommandService notiCommandService;

  @BeforeEach
  void setUp() {
    this.routeQueryService = mock(RouteQueryService.class);
    this.memberQueryService = mock(MemberQueryService.class);
    this.notifier = mock(NotificationProcessor.class);
    this.notiCommandService = mock(NotiCommandService.class);
    this.eventService = new EventService(routeQueryService, notifier, memberQueryService,
        notiCommandService);
  }

  @Test
  void handleLostItemSaveEvent_shouldProcessNotificationsForMembersInRoute() {
    // given
    var lostItemPayload = mock(LostItemPayload.class);
    given(lostItemPayload.createdAt()).willReturn(LocalDateTime.now());
    given(lostItemPayload.id()).willReturn(0);
    given(lostItemPayload.situationDescription()).willReturn("Test description");

    var lostItemSaveEvent = mock(LostItemSaveEvent.class);
    given(lostItemSaveEvent.saved()).willReturn(lostItemPayload);
    given(lostItemSaveEvent.getType()).willReturn("lostItemSaveEvent");

    // when
    this.eventService.handleLostItemSaveEvent(lostItemSaveEvent);

    // then
    verify(this.routeQueryService).findUserIdsNearPath(any(MembersInRouteQuery.class));
    verify(this.notifier).processNotifications(any(), any(), anyString(), anyString(),
        any());
  }

  @Test
  void handleRouteSavedEvent_shouldNotifyMemberWithFcmByRouteSavedEventType() {
    // given
    var routeSavedEvent = new RouteSavedEvent(1, "snapShot");
    given(this.memberQueryService.findMemberFcmById(1)).willReturn("1");

    // when
    this.eventService.handleRouteSavedEvent(routeSavedEvent);

    // then
    verify(this.notifier).notify(eq("1"), eq(routeSavedEvent.getType()), anyString());
  }
}
