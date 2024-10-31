package com.e205.command.bag.command;

public record BagDeleteCommand(
    Integer memberBagId,
    Integer bagId
) {

}
