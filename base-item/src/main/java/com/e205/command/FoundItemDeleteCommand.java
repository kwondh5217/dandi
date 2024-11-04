package com.e205.command;

public record FoundItemDeleteCommand(
    Integer memberId,
    Integer foundId
) {

}
