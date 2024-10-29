package com.e205.query;

public record LostItemValidRangeQuery(
    Integer memberId,
    Integer startRouteId,
    Integer endRouteId
) {

}
