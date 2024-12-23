package com.e205.base.member.command.bag.command;

import lombok.Builder;

@Builder
public record BagNameUpdateCommand(
    Integer memberId,
    Integer bagId,
    String name
) {

}

