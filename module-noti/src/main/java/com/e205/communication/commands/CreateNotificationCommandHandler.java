package com.e205.communication.commands;

import com.e205.commands.Command;
import com.e205.commands.CommandHandler;
import com.e205.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateNotificationCommandHandler implements CommandHandler<CreateNotificationCommand> {

  private final NotificationRepository notificationRepository;

  @Override
  public void handle(Command command) {
    this.notificationRepository.save(null);
  }
}
