package com.e205;

import com.e205.commands.Command;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CreateNotificationCommand implements Command {

  private Integer memberId;
  private Integer resourceId;
  private String title;
  private LocalDateTime createdAt;
  private String type;

  @Override
  public String getType() {
    return "createNotification";
  }
}
