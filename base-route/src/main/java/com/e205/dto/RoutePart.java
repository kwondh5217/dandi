package com.e205.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.locationtech.jts.geom.LineString;

@Builder
public record RoutePart(
    Integer id,
    LineString track,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
