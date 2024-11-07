package com.e205.command;

public record LostItemGrantCommand(
    Integer memberId,
    Integer lostId
) {

}
