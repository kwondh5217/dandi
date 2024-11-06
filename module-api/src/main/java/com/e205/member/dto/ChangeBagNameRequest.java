package com.e205.member.dto;

import com.e205.command.bag.command.BagNameUpdateCommand;

public record ChangeBagNameRequest(
    Integer bagId,
    String name,
    Integer memberId
) {
  public BagNameUpdateCommand toCommand() {
    return BagNameUpdateCommand.builder()
        .bagId(bagId)
        .name(name)
        .memberId(memberId)
        .build();
  }
}
