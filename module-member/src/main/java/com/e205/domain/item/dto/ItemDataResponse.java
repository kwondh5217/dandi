package com.e205.domain.item.dto;

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
