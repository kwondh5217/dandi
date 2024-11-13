package com.e205.service;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.CommentSaveCommand;
import com.e205.DeleteNotificationsCommand;
import com.e205.ItemCommandService;
import com.e205.ConfirmItemCommand;
import com.e205.command.bag.payload.MemberPayload;
import com.e205.command.member.payload.EmailStatus;
import com.e205.command.member.payload.MemberStatus;
import com.e205.command.member.service.MemberQueryService;
import com.e205.entity.LostItemNotification;
import com.e205.events.EventPublisher;
import com.e205.repository.CommentNotificationRepository;
import com.e205.repository.FoundItemNotificationRepository;
import com.e205.repository.LostItemNotificationRepository;
import com.e205.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotiCommandServiceTest {

  private NotificationRepository repository;
  private ItemCommandService itemCommandService;
  private NotiCommandService notiCommandService;
  private LostItemNotificationRepository lostItemNotificationRepository;
  private FoundItemNotificationRepository foundItemNotificationRepository;
  private CommentNotificationRepository commentNotificationRepository;
  private MemberQueryService memberQueryService;
  private EventPublisher eventPublisher;

  @BeforeEach
  void setUp() {
    this.repository = mock(NotificationRepository.class);
    this.itemCommandService = mock(ItemCommandService.class);
    this.lostItemNotificationRepository = mock(LostItemNotificationRepository.class);
    this.foundItemNotificationRepository = mock(FoundItemNotificationRepository.class);
    this.commentNotificationRepository = mock(CommentNotificationRepository.class);
    this.memberQueryService = mock(MemberQueryService.class);
    this.eventPublisher = mock(EventPublisher.class);
    this.notiCommandService = new NotiCommandService(
        repository,
        itemCommandService,
        lostItemNotificationRepository,
        foundItemNotificationRepository,
        commentNotificationRepository,
        eventPublisher,
        memberQueryService
    );
  }

  @Test
  void deleteNotifications_shouldDeleteNotificationsByIds() {
    List<Integer> notificationIds = List.of(1, 2, 3);
    var command = new DeleteNotificationsCommand(1,
        notificationIds);

    this.notiCommandService.deleteNotifications(command);

    verify(this.repository).deleteAllByIdAndMemberId(any(), anyList());
  }

  @Test
  void confirmItemNotification_shouldConfirmReadStatusForLostItemNotifications() {
    var confirmItemCommand = new ConfirmItemCommand(1, "lostItem");
    var lostItemNotification = mock(LostItemNotification.class);
    given(this.lostItemNotificationRepository.findByLostItemId(any())).willReturn(
        List.of(lostItemNotification));

    this.notiCommandService.confirmItemNotification(confirmItemCommand);

    verify(this.lostItemNotificationRepository).findByLostItemId(1);
    verify(lostItemNotification).confirmRead();
  }

  @Test
  void createCommentNotification() {
    // given
    Set<Integer> senders = Set.of(2, 3, 4);
    var command = new CommentSaveCommand(1, 1, senders, "lostComment");
    List<MemberPayload> memberPayloads = List.of(
        createMemberPayload(2, "User1", "user1@example.com", "fcmCode1", true, false, true),
        createMemberPayload(3, "User2", "user2@example.com", "fcmCode2", false, true, true),
        createMemberPayload(4, "User3", "user3@example.com", "fcmCode3", true, true, false)
    );
    given(this.memberQueryService.findMembers(any())).willReturn(memberPayloads);

    // when
    this.notiCommandService.createCommentNotification(command);

    // then
    verify(this.commentNotificationRepository, times(senders.size())).save(any());

    // Only two members have commentAlarm enabled
    verify(this.eventPublisher, times(2)).publishAtLeastOnce(any());
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