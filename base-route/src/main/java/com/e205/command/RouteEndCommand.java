package com.e205.command;

import com.e205.dto.TrackPoint;
import java.util.List;
import lombok.Builder;

@Builder
public record RouteEndCommand(
    Integer routeId,
    List<TrackPoint> points
) {

}
