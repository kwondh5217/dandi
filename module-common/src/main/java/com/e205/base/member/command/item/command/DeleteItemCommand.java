package com.e205.base.member.command.item.command;

public record DeleteItemCommand(
    Integer memberId,
    Integer itemId
) {

}
