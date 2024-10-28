package com.e205;

import com.e205.commands.Command;
import java.util.List;

public record ItemNotificationSaveCommand(
    List<Integer> membersId
) implements Command {

  @Override
  public String getType() {
    return "itemNotificationSave";
  }
}
