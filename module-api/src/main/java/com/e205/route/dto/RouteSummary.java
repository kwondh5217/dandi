package com.e205.route.dto;

import com.e205.geo.dto.Point;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RouteSummary(
    Integer id,
    String startAddress,
    String endAddress,
    List<Point> track,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
