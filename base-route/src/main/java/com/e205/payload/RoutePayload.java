package com.e205.payload;

import com.e205.dto.Snapshot;
import java.time.LocalDateTime;
import lombok.Builder;
import org.locationtech.jts.geom.LineString;

@Builder
public record RoutePayload(
    Integer id,
    Integer memberId,
    LineString track,
    char skip,
    Snapshot startSnapshot,
    Snapshot nextSnapshot,
    Integer previousRouteId,
    Integer nextRouteId,
    LocalDateTime createdAt,
    LocalDateTime endedAt
) {

}
