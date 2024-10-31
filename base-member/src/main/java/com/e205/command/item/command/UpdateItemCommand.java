package com.e205.command.item.command;

public record UpdateItemCommand(int memberId, int itemId, String emoticon, String name, byte colorKey) {

}
