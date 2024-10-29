package com.e205.command;

import java.time.LocalDateTime;

public record RouteCreateCommand(
    Integer bagId,
    LocalDateTime startTime
) {

}
