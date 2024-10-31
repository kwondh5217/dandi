package com.e205.command.bag.command;

public record CopyBagCommand(
    Integer memberId,
    Integer bagsId,
    String newBagName
) {

}
