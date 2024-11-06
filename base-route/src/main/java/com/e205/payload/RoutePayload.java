package com.e205.payload;

import com.e205.dto.Snapshot;
import com.e205.dto.TrackPoint;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RoutePayload(
    Integer id,
    Integer memberId,
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
