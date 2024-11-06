package com.e205.member.dto;

import com.e205.command.bag.command.CopyBagCommand;

public record CopySelectBagRequest(
    Integer bagId,
    String newBagName,
    Integer memberId
) {
  public CopyBagCommand toCommand() {
    return CopyBagCommand.builder()
        .bagsId(bagId)
        .memberId(memberId)
        .newBagName(newBagName)
        .build();
  }
}
