package com.e205.command.bag.command;

public record BagItemDeleteCommand(
    Integer memberId,
    Integer memberMainBagId,
    Integer bagId,
    Integer bagItemId
) {

}
