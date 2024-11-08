package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.CreateNotificationCommand;
import com.e205.MemberWithFcm;
import com.e205.NotifiedMembersCommand;
import com.e205.NotifyOutboxEvent;
import com.e205.events.EventPublisher;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class NotificationProcessorTest {

  private NotiCommandService notiCommandService;
  private Notifier notifier;
  private EventPublisher eventPublisher;
  private NotificationProcessor notificationProcessor;

  @BeforeEach
  void setUp() {
    TransactionSynchronizationManager.initSynchronization();
    this.notiCommandService = mock(NotiCommandService.class);
    this.notifier = mock(Notifier.class);
    this.eventPublisher = mock(EventPublisher.class);
    this.notificationProcessor = new NotificationProcessor(notiCommandService,
        notifier, eventPublisher);
  }

  @Test
  void processNotifications_shouldCreateNotificationsForEachMember() {
    // given
    List<MemberWithFcm> membersWithFcm = List.of(
        new MemberWithFcm(1, "token1"),
        new MemberWithFcm(2, "token2"),
        new MemberWithFcm(3, "token3")
    );

    // when
    List<NotifiedMembersCommand> notifiedMembers = this.notificationProcessor.processNotifications(
        1, "Test Description", "lostItemSaveEvent", membersWithFcm);

    // then
    verify(this.notiCommandService, times(membersWithFcm.size()))
        .createNotification(any(CreateNotificationCommand.class));
    verify(this.eventPublisher, times(membersWithFcm.size())).publishAtLeastOnce(any(
        NotifyOutboxEvent.class));
    assertThat(notifiedMembers).hasSize(membersWithFcm.size());

  }

  @Test
  void notify_shouldDirectlyInvokeNotifierWithCorrectParameters() {
    // given
    String fcmToken = "fcmToken";
    String title = "Test Title";
    String body = "Test Body";

    // when
    notificationProcessor.notify(fcmToken, title, body);

    // then
    verify(notifier, times(1)).notify(eq(fcmToken), eq(title), eq(body));
  }
}
