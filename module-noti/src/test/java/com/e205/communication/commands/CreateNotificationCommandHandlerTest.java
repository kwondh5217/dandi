package com.e205.communication.commands;

import static org.mockito.Mockito.*;

import com.e205.repository.NotificationRepository;
import org.junit.jupiter.api.Test;

class CreateNotificationCommandHandlerTest {

  @Test
  void createNotification() {
    // given
    var repository = mock(NotificationRepository.class);
    var handler = new CreateNotificationCommandHandler(repository);
    var command = mock(CreateNotificationCommand.class);

    // when
    handler.handle(command);

    // then
    verify(repository).save(any());
  }

}