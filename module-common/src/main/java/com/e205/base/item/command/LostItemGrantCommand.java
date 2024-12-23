package com.e205.base.item.command;

public record LostItemGrantCommand(
    Integer memberId,
    Integer lostId
) {

}
