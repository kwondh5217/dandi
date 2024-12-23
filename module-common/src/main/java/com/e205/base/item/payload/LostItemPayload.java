package com.e205.base.item.payload;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record LostItemPayload(
    Integer id,
    Integer memberId,
    Integer startRouteId,
    Integer endRouteId,
    String situationDescription,
    String itemDescription,
    LocalDateTime lostAt,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
