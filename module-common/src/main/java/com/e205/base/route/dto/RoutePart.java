package com.e205.base.route.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RoutePart(
    Integer id,
    String startAddress,
    String endAddress,
    List<TrackPoint> track,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
