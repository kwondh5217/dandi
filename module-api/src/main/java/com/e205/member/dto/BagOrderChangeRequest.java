package com.e205.member.dto;

import com.e205.base.member.command.bag.command.BagOrderCommand;

public record BagOrderChangeRequest(
    Integer bagId,
    int order
) {
  public BagOrderCommand toCommand() {
    return BagOrderCommand.builder()
        .bagId(this.bagId())
        .order((byte) this.order())
        .build();
  }
}
