package com.e205.command.bag.command;

import lombok.Builder;

@Builder
public record BagOrderCommand(
    Integer bagId,
    Byte order
) {

}
