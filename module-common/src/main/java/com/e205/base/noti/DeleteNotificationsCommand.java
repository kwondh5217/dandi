package com.e205.base.noti;

import java.util.List;
import lombok.NonNull;
import org.springframework.util.Assert;

public record DeleteNotificationsCommand(@NonNull Integer memberId,
                                         List<Integer> notificationIds) {
  public DeleteNotificationsCommand {
    Assert.notEmpty(notificationIds, "notificationIds cannot be empty");
  }
}
