package com.e205.member.dto;

import com.e205.base.member.command.bag.command.AddItemsToBagCommand;
import java.util.List;

public record AddItemsToBagRequest(
    Integer bagId,
    Integer memberId,
    List<Integer> itemIds
) {

  public AddItemsToBagCommand toCommand() {
    return AddItemsToBagCommand.builder()
        .bagId(bagId)
        .memberId(memberId)
        .itemIds(itemIds)
        .build();
  }
}
