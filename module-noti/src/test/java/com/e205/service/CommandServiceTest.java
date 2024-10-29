package com.e205.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.DeleteNotificationsCommand;
import com.e205.communication.ItemCommandService;
import com.e205.repository.NotificationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommandServiceTest {

  @Test
  void deleteNotificationMembers() {
    var repository = mock(NotificationRepository.class);
    var itemService = mock(ItemCommandService.class);
    var commandService = new CommandService(repository, itemService);
    List<Integer> notificationIds = List.of(1, 2, 3);
    var command = new DeleteNotificationsCommand(
        notificationIds);

    commandService.deleteNotifications(command);

    verify(repository).deleteAllByIdInBatch(notificationIds);
  }

}