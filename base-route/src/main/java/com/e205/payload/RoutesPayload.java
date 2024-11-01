package com.e205.payload;

import com.e205.dto.RoutePart;
import com.e205.dto.Snapshot;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.locationtech.jts.geom.LineString;

@Builder
public record RoutesPayload(
    List<RoutePart> routeParts,
    Integer nextRouteId
) {

}
