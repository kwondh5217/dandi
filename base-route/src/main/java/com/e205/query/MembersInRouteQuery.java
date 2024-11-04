package com.e205.query;

public record MembersInRouteQuery(
    Integer memberId,
    Integer startRouteId,
    Integer endRouteId
) {

}
