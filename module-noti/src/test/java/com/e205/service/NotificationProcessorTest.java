package com.e205.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.CreateNotificationCommand;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class NotificationProcessorTest {

  private NotiCommandService notiCommandService;
  private Notifier notifier;
  private ApplicationEventPublisher eventPublisher;
  private NotificationProcessor notificationProcessor;

  @BeforeEach
  void setUp() {
    notiCommandService = mock(NotiCommandService.class);
    notifier = mock(Notifier.class);
    eventPublisher = mock(ApplicationEventPublisher.class);
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
    notificationProcessor.processNotifications(
        1, 4,"Test Description", "lostItemSaveEvent", memberPayloads);

    // then
    verify(notiCommandService, times(memberPayloads.size()))
        .createNotification(any(CreateNotificationCommand.class));
  }

  @Test
  void notify_shouldDirectlyInvokeNotifierWithCorrectParameters() throws Exception {
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
