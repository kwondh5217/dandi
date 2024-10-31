package com.e205.command.bag.command;

public record BagItemOrderCommand(
    Integer itemId,
    Byte order
) {

}
