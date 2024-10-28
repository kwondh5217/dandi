package com.e205.communication;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.CreateNotificationCommand;
import com.e205.NotificationDispatcher;
import com.e205.commands.Command;
import com.e205.service.CommandService;
import com.e205.service.EventService;
import com.e205.service.QueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationDispatcherTest {

  private NotificationDispatcher dispatcher;
  private CommandService commandService;
  private QueryService queryService;
  private EventService eventService;

  @BeforeEach
  void setUp() {
    commandService = mock(CommandService.class);
    queryService = mock(QueryService.class);
    eventService = mock(EventService.class);

    dispatcher = new NotificationDispatcher(commandService, queryService, eventService);

    // @PostConstruct 메서드 수동 호출
    dispatcher.init();
  }

  @Test
  void createNotification() {
    // given
    var command = mock(CreateNotificationCommand.class);
    given(command.getType()).willReturn("createNotification");

    // when
    dispatcher.dispatchMethod(command);

    // then
    verify(commandService).createNotification(any(CreateNotificationCommand.class));
  }

  @Test
  void createNotificationNotSupportCommandType() {
    // given
    var command = mock(Command.class);
    given(command.getType()).willReturn("unsupportedCommandType");

    // expect
    assertThatThrownBy(() -> dispatcher.dispatchMethod(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No handler found for command type: unsupportedCommandType");
  }
}
