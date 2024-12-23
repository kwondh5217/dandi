package com.e205.base.item.query;

public record LostItemValidRangeQuery(
    Integer memberId,
    Integer startRouteId,
    Integer endRouteId
) {

}
