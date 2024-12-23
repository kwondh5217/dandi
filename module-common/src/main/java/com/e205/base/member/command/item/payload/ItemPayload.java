package com.e205.base.member.command.item.payload;

public record ItemPayload(
    Integer id,
    Integer memberId,
    String emoticon,
    String name,
    byte colorKey,
    byte itemOrder
) {

}