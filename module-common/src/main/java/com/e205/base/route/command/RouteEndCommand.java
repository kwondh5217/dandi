package com.e205.base.route.command;

import com.e205.base.route.dto.TrackPoint;
import java.util.List;
import lombok.Builder;

@Builder
public record RouteEndCommand(
    Integer memberId,
    Integer routeId,
    List<TrackPoint> points,
    String startAddress,
    String endAddress
) {

}
