package com.e205.base.route.payload;

import lombok.Builder;

@Builder
public record RouteIdPayload(
    Integer routeId
) {

}
