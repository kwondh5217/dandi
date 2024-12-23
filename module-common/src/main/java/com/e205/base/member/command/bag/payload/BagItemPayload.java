package com.e205.base.member.command.bag.payload;

public record BagItemPayload(
    Integer id,
    Integer bagId,
    Integer itemId,
    Byte itemOrder
) {

}