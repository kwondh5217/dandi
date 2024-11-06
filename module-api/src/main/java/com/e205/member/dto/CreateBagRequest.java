package com.e205.member.dto;

import com.e205.command.bag.command.CreateBagCommand;

public record CreateBagRequest(
    Integer memberId,
    String name
) {

  public CreateBagCommand toCommand() {
    return CreateBagCommand.builder()
        .memberId(memberId)
        .name(name)
        .build();
  }
}
