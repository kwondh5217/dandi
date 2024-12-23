package com.e205.base.member.command.bag.payload;

public record BagPayload(
    Integer id,
    Integer memberId,
    char enabled,
    Byte bagOrder,
    String name
) {

}