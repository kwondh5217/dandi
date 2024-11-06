package com.e205.member.dto;

import com.e205.command.bag.command.BagDeleteCommand;

public record DeleteBagRequest(
    Integer bagId,
    Integer memberId
) {

  public BagDeleteCommand toCommand(Integer memberId, Integer memberBagId) {
    return BagDeleteCommand.builder()
        .bagId(bagId)
        .memberId(memberId)
        .memberBagId(memberBagId)
        .build();
  }
}
