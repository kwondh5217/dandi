package com.e205.base.noti;

import java.util.Set;
import lombok.NonNull;
import org.springframework.util.Assert;

public record CommentSaveCommand(
    @NonNull Integer commentId,
    @NonNull Integer writerId,
    Set<Integer> senders,
    String type
){
  public CommentSaveCommand {
    Assert.notEmpty(senders, "senders cannot be empty");
    Assert.hasText(type, " type cannot be empty");
  }
}
