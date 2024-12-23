package com.e205.base.route.query;

import java.time.LocalDate;

public record DailyRouteReadQuery(
    Integer memberId,
    LocalDate date
) {

}
