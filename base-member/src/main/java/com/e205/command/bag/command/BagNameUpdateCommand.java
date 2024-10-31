package com.e205.command.bag.command;

public record BagNameUpdateCommand(
    Integer memberId,
    Integer bagId,
    String name
) {

}

