package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.CreateNotificationCommand;
import com.e205.NotifiedMembersCommand;
import com.e205.NotifyOutboxEvent;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import com.e205.events.EventPublisher;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationProcessorTest {

  private NotiCommandService notiCommandService;
  private Notifier notifier;
  private EventPublisher eventPublisher;
  private NotificationProcessor notificationProcessor;

  @BeforeEach
  void setUp() {
    notiCommandService = mock(NotiCommandService.class);
    notifier = mock(Notifier.class);
    eventPublisher = mock(EventPublisher.class);
    notificationProcessor = new NotificationProcessor(notiCommandService, notifier, eventPublisher);
  }

  @Test
  void processNotifications_shouldCreateNotificationsForEachMember_withSelectivePushNotifications() {
    // given
    List<MemberPayload> memberPayloads = List.of(
        createMemberPayload(1, "User1", "user1@example.com", "fcmCode1", true, false, true),
        createMemberPayload(2, "User2", "user2@example.com", "fcmCode2", false, true, false),
        createMemberPayload(3, "User3", "user3@example.com", "fcmCode3", true, true, false)
    );

    // when
    List<NotifiedMembersCommand> notifiedMembers = notificationProcessor.processNotifications(
        1, "Test Description", "lostItemSaveEvent", memberPayloads);

    // then
    verify(notiCommandService, times(memberPayloads.size()))
        .createNotification(any(CreateNotificationCommand.class));

    // Only two members have lostItemAlarm enabled
    verify(eventPublisher, times(2)).publishAtLeastOnce(any(NotifyOutboxEvent.class));
    assertThat(notifiedMembers).hasSize(memberPayloads.size());
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

  private MemberPayload createMemberPayload(
      int id, String nickname, String email, String fcmCode,
      boolean foundItemAlarm, boolean lostItemAlarm, boolean commentAlarm) {

    return MemberPayload.builder()
        .id(id)
        .bagId(id)
        .nickname(nickname)
        .email(email)
        .status(EmailStatus.VERIFIED)
        .memberStatus(MemberStatus.ACTIVE)
        .fcmCode(fcmCode)
        .foundItemAlarm(foundItemAlarm)
        .lostItemAlarm(lostItemAlarm)
        .commentAlarm(commentAlarm)
        .build();
  }
}
