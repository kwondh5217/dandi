package com.e205.base.route.dto;

import lombok.Builder;

@Builder
public record TrackPoint(
    double lat,
    double lon
) {

}
