package com.e205;

import com.e205.querys.Query;
import java.util.List;
import lombok.NonNull;
import org.springframework.util.Assert;

public record QueryNotificationWithCursor(
    @NonNull Integer memberId,
    @NonNull Integer lastResourceId,
    List<String> types
) implements Query {

  public QueryNotificationWithCursor {
    Assert.notEmpty(types, "types cannot be empty");
  }
  @Override
  public String getType() {
    return "findNotificationWithCursor";
  }
}
