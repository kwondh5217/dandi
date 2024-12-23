package com.e205.base.member.command.bag.command;

import lombok.Builder;

@Builder
public record SelectBagCommand(
    Integer myBagId,
    Integer targetBagId,
    Integer memberId
) {

}
