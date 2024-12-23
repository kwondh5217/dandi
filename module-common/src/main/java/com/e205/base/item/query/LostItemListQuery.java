package com.e205.base.item.query;

import com.e205.querys.Query;

public record LostItemListQuery(
    Integer memberId
) implements Query {

  @Override
  public String getType() {
    return "lostItemListQuery";
  }
}
