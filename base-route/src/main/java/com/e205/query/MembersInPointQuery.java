package com.e205.query;

import lombok.Builder;

@Builder
public record MembersInPointQuery(
    Double lat,
    Double lon,
    Integer subtractionTime
) {

}
