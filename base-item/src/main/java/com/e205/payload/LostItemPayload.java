package com.e205.payload;

import java.time.LocalDateTime;
import lombok.Builder;
import org.locationtech.jts.geom.LineString;

@Builder
public record LostItemPayload(
    Integer id,
    Integer memberId,
    LineString route,
    String situationDescription,
    String itemDescription,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {
}
