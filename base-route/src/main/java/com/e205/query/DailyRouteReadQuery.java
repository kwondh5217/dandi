package com.e205.query;

import java.time.LocalDate;

public record DailyRouteReadQuery(
    Integer memberId,
    LocalDate date
) {

}
