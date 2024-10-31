package com.e205.command.item.command;

public record CreateItemCommand(Integer bagId, String emoticon, String name,
                                byte colorKey, Integer memberId) {

}
