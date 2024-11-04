package com.e205.item.dto;

import java.time.LocalDateTime;

public record LostItemCreateRequest(
    String situationDesc,
    String itemDesc,
    int startRoute,
    int endRoute,
    LocalDateTime lostAt
) {

}
