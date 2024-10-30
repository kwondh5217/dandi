package com.e205.command;

import com.e205.dto.TrackPointPayload;
import java.time.LocalDateTime;
import java.util.List;

public record RouteEndCommand(
    Integer routeId,
    LocalDateTime endedAt,
    List<TrackPointPayload> points
) {

}
