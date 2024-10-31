package com.e205.command.item.command;

public record DeleteItemCommand(
    Integer memberId,
    Integer itemId
) {

}
