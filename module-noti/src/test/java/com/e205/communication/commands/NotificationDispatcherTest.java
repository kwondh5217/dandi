package com.e205.communication.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

class NotificationDispatcherTest {

  @Test
  void dispatchCommand() {
    // given
    var handler = mock(CreateNotificationCommandHandler.class);
    var dispatcher = new NotificationDispatcher();
    var command = new CreateNotificationCommand();
    dispatcher.registerHandler(command.getType(), handler);

    // when
    dispatcher.dispatch(command);

    // then
    verify(handler).handle(command);
  }

}