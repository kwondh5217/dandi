package com.e205.base.item.command;

public record FoundItemDeleteCommand(
    Integer memberId,
    Integer foundId
) {

}
