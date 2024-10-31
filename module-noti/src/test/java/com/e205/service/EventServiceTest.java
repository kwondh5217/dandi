package com.e205.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.MemberWithFcm;
import com.e205.MembersInRouteQuery;
import com.e205.communication.MemberQueryService;
import com.e205.communication.RouteQueryService;
import com.e205.event.LostItemSaveEvent;
import com.e205.event.RouteSavedEvent;
import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventServiceTest {

  private RouteQueryService routeQueryService;
  private MemberQueryService memberQueryService;
  private NotificationProcessor notifier;
  private EventService eventService;

  @BeforeEach
  void setUp() {
    this.routeQueryService = mock(RouteQueryService.class);
    this.memberQueryService = mock(MemberQueryService.class);
    this.notifier = mock(NotificationProcessor.class);
    this.eventService = new EventService(routeQueryService, notifier, memberQueryService);
  }

  @Test
  void handleLostItemSaveEvent() {
    // given
    var lostItemPayload = mock(LostItemPayload.class);
    given(lostItemPayload.createdAt()).willReturn(LocalDateTime.now());
    given(lostItemPayload.id()).willReturn(0);
    given(lostItemPayload.situationDescription()).willReturn("Test description");
    LostItemSaveEvent lostItemSaveEvent = mock(LostItemSaveEvent.class);
    given(lostItemSaveEvent.saved()).willReturn(lostItemPayload);
    given(lostItemSaveEvent.getType()).willReturn("lostItemSaveEvent");

    List<MemberWithFcm> memberWithFcms = List.of(
        new MemberWithFcm(1, "1"),
        new MemberWithFcm(2, "2")
    );
    given(this.memberQueryService.membersWithFcmQuery(any())).willReturn(
        memberWithFcms);

    // when
    this.eventService.handleLostItemSaveEvent(lostItemSaveEvent);

    // then
    verify(this.routeQueryService).queryMembersInPoints(any(MembersInRouteQuery.class));
    verify(this.notifier).processNotifications(any(), anyString(), anyString(), any());
  }

  @Test
  void handleRouteSavedEvent() {
    var routeSavedEvent = new RouteSavedEvent(1, 1, "snapShot");
    given(this.memberQueryService.findMemberFcmById(1)).willReturn("1");

    this.eventService.handleRouteSavedEvent(routeSavedEvent);

    verify(this.notifier).notify(eq("1"), eq(routeSavedEvent.getType()), anyString());
  }
}
