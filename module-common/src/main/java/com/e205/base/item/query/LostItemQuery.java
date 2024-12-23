package com.e205.base.item.query;

import com.e205.querys.Query;

public record LostItemQuery(
    Integer memberId,
    Integer lostItemId
) implements Query {

  @Override
  public String getType() {
    return "lostItemQuery";
  }
}
