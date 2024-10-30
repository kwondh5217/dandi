package com.e205.payload;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record LostItemPayload(
    Integer id,
    Integer memberId,
    int startRouteId,
    int endRouteId,
    String situationDescription,
    String itemDescription,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
