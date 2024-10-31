package com.e205.service;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.DeleteNotificationsCommand;
import com.e205.command.ConfirmItemCommand;
import com.e205.communication.ItemCommandService;
import com.e205.entity.LostItemNotification;
import com.e205.repository.FoundItemNotificationRepository;
import com.e205.repository.LostItemNotificationRepository;
import com.e205.repository.NotificationRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandServiceTest {

  private NotificationRepository repository;
  private ItemCommandService itemCommandService;
  private CommandService commandService;
  private LostItemNotificationRepository lostItemNotificationRepository;
  private FoundItemNotificationRepository foundItemNotificationRepository;

  @BeforeEach
  void setUp() {
    this.repository = mock(NotificationRepository.class);
    this.itemCommandService = mock(ItemCommandService.class);
    this.lostItemNotificationRepository = mock(LostItemNotificationRepository.class);
    this.foundItemNotificationRepository = mock(FoundItemNotificationRepository.class);
    this.commandService = new CommandService(
        repository,
        itemCommandService,
        lostItemNotificationRepository,
        foundItemNotificationRepository
    );
  }

  @Test
  void deleteNotificationMembers() {
    List<Integer> notificationIds = List.of(1, 2, 3);
    var command = new DeleteNotificationsCommand(
        notificationIds);

    this.commandService.deleteNotifications(command);

    verify(this.repository).deleteAllByIdInBatch(notificationIds);
  }

  @Test
  void confirmationNotification_lostItem() {
    var confirmItemCommand = new ConfirmItemCommand(1, "lostItem");
    var lostItemNotification = mock(LostItemNotification.class);
    given(this.lostItemNotificationRepository.findByLostItemId(any())).willReturn(
        List.of(lostItemNotification));

    this.commandService.confirmItemNotification(confirmItemCommand);

    verify(this.lostItemNotificationRepository).findByLostItemId(1);
    verify(lostItemNotification).confirmRead();
  }

}