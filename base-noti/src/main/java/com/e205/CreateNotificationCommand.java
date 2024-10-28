package com.e205;

import com.e205.commands.Command;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateNotificationCommand implements Command {

  private Integer memberId;
  private Integer resourceId;
  private String title;
  private LocalDateTime createdAt;
  private String type;

  @Builder
  public CreateNotificationCommand(Integer memberId, Integer resourceId, String title,
      LocalDateTime createdAt, String type) {
    this.memberId = memberId;
    this.resourceId = resourceId;
    this.title = title;
    this.createdAt = createdAt;
    this.type = type;
  }

  @Override
  public String getType() {
    return "createNotification";
  }
}
