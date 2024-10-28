package com.e205.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.e205.MemberWithFcm;
import com.e205.MembersInRouteQuery;
import com.e205.communication.MemberQueryService;
import com.e205.communication.RouteQueryService;
import com.e205.event.LostItemSaveEvent;
import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventServiceTest {

  private LostItemPayload lostItemPayload;
  private RouteQueryService routeQueryService;
  private MemberQueryService memberQueryService;
  private CommandService commandService;
  private Notifier notifier;
  private EventService eventService;

  @BeforeEach
  void setUp() {
    this.lostItemPayload = mock(LostItemPayload.class);
    given(lostItemPayload.route()).willReturn(
        mock(org.locationtech.jts.geom.LineString.class));
    given(lostItemPayload.createdAt()).willReturn(LocalDateTime.now());

    this.routeQueryService = mock(RouteQueryService.class);
    this.memberQueryService = mock(MemberQueryService.class);
    this.commandService = mock(CommandService.class);
    this.notifier = mock(Notifier.class);
    this.eventService = new EventService(routeQueryService, notifier,
        memberQueryService, commandService);
  }

  @Test
  void handleLostItemSaveEvent() {
    // given
    LostItemSaveEvent lostItemSaveEvent = mock(LostItemSaveEvent.class);
    when(lostItemSaveEvent.saved()).thenReturn(lostItemPayload);

    given(this.memberQueryService.memberWithFcmQuery(any())).willReturn(
        List.of(new MemberWithFcm(1, "1")));

    // when
    this.eventService.handleLostItemSaveEvent(lostItemSaveEvent);

    // then
    verify(this.routeQueryService).queryMembersInPoints(any(MembersInRouteQuery.class));
    verify(this.notifier).notify(any());
    verify(this.commandService).createNotification(any());

  }
}