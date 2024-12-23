package com.e205.base.member.command.item.temp;

import java.time.LocalDateTime;

public record ItemDataResponse(
    Integer id,
    byte itemOrder,
    String emoticon,
    String name,
    byte colorKey,
    LocalDateTime createdAt
) {

}
