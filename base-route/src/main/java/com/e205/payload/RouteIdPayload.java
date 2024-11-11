package com.e205.payload;

import lombok.Builder;

@Builder
public record RouteIdPayload(
    Integer routeId
) {

}
