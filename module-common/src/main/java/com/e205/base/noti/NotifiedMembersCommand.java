package com.e205.base.noti;

import java.time.LocalDateTime;
import lombok.NonNull;
import org.springframework.util.Assert;

public record NotifiedMembersCommand(@NonNull Integer memberId, @NonNull Integer resourceId,
                                     @NonNull LocalDateTime createdAt, @NonNull String type) {
  public NotifiedMembersCommand {
    Assert.hasText(type, "type must not be empty");
  }
}

