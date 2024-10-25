package com.e205.communication.commands;

import com.e205.commands.Command;

public class CreateNotificationCommand implements Command {

  @Override
  public String getType() {
    return "createNotification";
  }
}
