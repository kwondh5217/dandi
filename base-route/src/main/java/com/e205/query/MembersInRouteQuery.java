package com.e205.query;

import java.time.LocalDateTime;

public record MembersInRouteQuery(
    Integer memberId,
    Integer startRouteId,
    Integer endRouteId,
    LocalDateTime since
) {

}
