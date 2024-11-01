package com.e205.command;

import com.e205.dto.TrackPoint;
import java.time.LocalDateTime;
import java.util.List;

public record RouteEndCommand(
    Integer routeId,
    LocalDateTime endedAt,
    List<TrackPoint> points
) {

}
