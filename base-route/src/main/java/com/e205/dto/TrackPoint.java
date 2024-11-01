package com.e205.dto;

import lombok.Builder;

@Builder
public record TrackPoint(
    double lat,
    double lon
) {

}
