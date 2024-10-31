package com.e205.command.bag.payload;

public record BagPayload(
    Integer id,
    Integer memberId,
    char enabled,
    Byte bagOrder,
    String name
) {

}