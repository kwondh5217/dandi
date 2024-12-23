package com.e205.base.member.command.bag.command;

import lombok.Builder;

@Builder
public record BagOrderCommand(
    Integer bagId,
    Byte order
) {

}
