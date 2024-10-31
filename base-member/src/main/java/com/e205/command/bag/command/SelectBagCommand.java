package com.e205.command.bag.command;

public record SelectBagCommand(
    Integer myBagId,
    Integer targetBagId,
    Integer memberId
) {

}
