package com.e205.command.bag.command;

import lombok.Builder;

@Builder
public record BagNameUpdateCommand(
    Integer memberId,
    Integer bagId,
    String name
) {

}

