package com.e205.base.member.command.bag.command;

public record BagItemDeleteCommand(
    Integer memberId,
    Integer bagId,
    Integer bagItemId
) {

}
