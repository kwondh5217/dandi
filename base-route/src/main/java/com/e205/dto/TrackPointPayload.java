package com.e205.dto;

import lombok.Builder;

@Builder
public record TrackPointPayload(
    double lat,
    double lon
) {

}
