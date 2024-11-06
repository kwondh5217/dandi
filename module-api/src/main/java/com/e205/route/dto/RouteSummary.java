package com.e205.route.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RouteSummary(
    Integer id,
    List<Point> track,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
