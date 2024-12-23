package com.e205.base.member.command.bag.command;

public record BagItemOrderCommand(
    Integer itemId,
    Byte order
) {

}
