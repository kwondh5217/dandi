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
  private String body;

  @Builder
  public CreateNotificationCommand(Integer memberId, Integer resourceId, String title,
      LocalDateTime createdAt, String type, String body) {
    this.memberId = memberId;
    this.resourceId = resourceId;
    this.title = title;
    this.createdAt = createdAt;
    this.type = type;
    this.body = body;
  }

  public String getNotiType() {
    return this.type;
  }

  @Override
  public String getType() {
    return "createNotification";
  }
}
