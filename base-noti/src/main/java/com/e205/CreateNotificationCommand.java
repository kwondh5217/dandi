package com.e205;

import com.e205.commands.Command;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.Assert;

@Getter
public class CreateNotificationCommand implements Command {

  private Integer memberId;
  private Integer resourceId;
  private String title;
  private LocalDateTime createdAt;
  private String type;
  private String body;

  @Builder
  public CreateNotificationCommand(@NonNull Integer memberId, @NonNull Integer resourceId,
      String title, @NonNull LocalDateTime createdAt, @NonNull String type, String body) {
    Assert.hasText(type, "type must not be empty");
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
