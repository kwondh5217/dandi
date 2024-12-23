package com.e205.base.route.query;

import lombok.Builder;

@Builder
public record MembersInPointQuery(
    Double lat,
    Double lon,
    Integer subtractionTime
) {

}
