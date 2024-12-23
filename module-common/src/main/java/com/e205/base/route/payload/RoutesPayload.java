package com.e205.base.route.payload;

import com.e205.base.route.dto.RoutePart;
import java.util.List;
import lombok.Builder;

@Builder
public record RoutesPayload(
    List<RoutePart> routeParts,
    Integer nextRouteId
) {

}
