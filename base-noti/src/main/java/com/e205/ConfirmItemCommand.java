package com.e205;

import lombok.NonNull;
import org.springframework.util.Assert;

public record ConfirmItemCommand(@NonNull Integer itemId, String type) {
  public ConfirmItemCommand {
    Assert.hasText(type, "Type must not be empty");
  }

}
