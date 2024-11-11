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
import com.e205.command.ConfirmItemCommand;
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
    Set<Integer> senders = Set.of(2, 3, 4);
    var command = new CommentSaveCommand(1, 1, senders, "lostComment");

    this.notiCommandService.createCommentNotification(command);

    verify(this.commentNotificationRepository, times(senders.size())).save(any());
    verify(this.memberQueryService, times(senders.size())).findMemberFcmById(any());
    verify(this.eventPublisher, times(senders.size())).publishAtLeastOnce(any());
  }

}