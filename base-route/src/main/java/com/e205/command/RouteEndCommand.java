package com.e205.command;

import com.e205.dto.TrackPoint;
import java.util.List;

public record RouteEndCommand(
    Integer routeId,
    List<TrackPoint> points
) {

}
