package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.MemberWithFcm;
import com.e205.MembersInRouteQuery;
import com.e205.communication.ItemCommandService;
import com.e205.communication.MemberQueryService;
import com.e205.communication.RouteQueryService;
import com.e205.event.LostItemSaveEvent;
import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class EventServiceTest {

  private LostItemPayload lostItemPayload;
  private RouteQueryService routeQueryService;
  private MemberQueryService memberQueryService;
  private CommandService commandService;
  private Notifier notifier;
  private EventService eventService;
  private RetryTemplate retryTemplate;
  private PlatformTransactionManager transactionManager;
  private ItemCommandService itemCommandService;

  @BeforeEach
  void setUp() {
    TransactionSynchronizationManager.initSynchronization();

    this.lostItemPayload = mock(LostItemPayload.class);
    given(lostItemPayload.route()).willReturn(
        mock(org.locationtech.jts.geom.LineString.class));
    given(lostItemPayload.createdAt()).willReturn(LocalDateTime.now());

    this.routeQueryService = mock(RouteQueryService.class);
    this.memberQueryService = mock(MemberQueryService.class);
    this.commandService = mock(CommandService.class);
    this.notifier = mock(Notifier.class);
    this.retryTemplate = mock(RetryTemplate.class);
    this.transactionManager = mock(PlatformTransactionManager.class);
    this.itemCommandService = mock(ItemCommandService.class);
    this.eventService = new EventService(routeQueryService, notifier,
        memberQueryService, commandService, transactionManager, retryTemplate, itemCommandService);
  }

  @AfterEach
  void tearDown() {
    TransactionSynchronizationManager.clearSynchronization();
  }

  @Test
  void handleLostItemSaveEvent() {
    // given
    LostItemSaveEvent lostItemSaveEvent = mock(LostItemSaveEvent.class);
    given(lostItemSaveEvent.saved()).willReturn(lostItemPayload);

    List<MemberWithFcm> memberWithFcms = List.of(
        new MemberWithFcm(1, "1"),
        new MemberWithFcm(2, "2")
    );
    given(this.memberQueryService.memberWithFcmQuery(any())).willReturn(
        memberWithFcms);

    // when
    this.eventService.handleLostItemSaveEvent(lostItemSaveEvent);

    // then
    verify(this.routeQueryService).queryMembersInPoints(any(MembersInRouteQuery.class));
    verify(this.commandService, times(memberWithFcms.size())).createNotification(any());

    var synchronizations = TransactionSynchronizationManager.getSynchronizations();
    assertThat(synchronizations).isNotEmpty();

    long notifierCallbackCount = synchronizations.stream()
        .filter(sync -> sync instanceof TransactionSynchronizationAdapter)
        .count();
    assertThat(notifierCallbackCount).isEqualTo(2);

    verify(this.itemCommandService).saveNotifiedMembers(any());
  }
}
