package com.e205.command.bag.command;

import lombok.Builder;

@Builder
public record SelectBagCommand(
    Integer myBagId,
    Integer targetBagId,
    Integer memberId
) {

}
