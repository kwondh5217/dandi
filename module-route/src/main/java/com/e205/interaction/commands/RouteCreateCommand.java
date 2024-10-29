package com.e205.interaction.commands;

import java.time.LocalDateTime;

public record RouteCreateCommand(
    Integer bagId,
    LocalDateTime startTime
) {

}
