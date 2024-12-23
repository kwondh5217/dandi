package com.e205.base.route.payload;

import com.e205.base.route.dto.Snapshot;
import com.e205.base.route.dto.TrackPoint;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RoutePayload(
    Integer id,
    Integer memberId,
    String startAddress,
    String endAddress,
    List<TrackPoint> track,
    char skip,
    Snapshot startSnapshot,
    Snapshot nextSnapshot,
    Integer previousRouteId,
    Integer nextRouteId,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
