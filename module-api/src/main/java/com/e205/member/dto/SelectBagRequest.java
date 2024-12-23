package com.e205.member.dto;

import com.e205.base.member.command.bag.command.SelectBagCommand;

public record SelectBagRequest(
    Integer bagId,
    Integer memberId
) {
  public SelectBagCommand toCommmand(Integer myBagId) {
    return SelectBagCommand.builder()
        .myBagId(myBagId)
        .targetBagId(bagId)
        .memberId(memberId)
        .build();
  }
}
