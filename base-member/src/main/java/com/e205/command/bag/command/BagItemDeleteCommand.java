package com.e205.command.bag.command;

public record BagItemDeleteCommand(
    Integer memberId,
    Integer bagId,
    Integer bagItemId
) {

}
