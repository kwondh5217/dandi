package com.e205.base.member.command.bag.command;

import lombok.Builder;

@Builder
public record CreateBagCommand(
    Integer memberId,
    String name
) {

}

