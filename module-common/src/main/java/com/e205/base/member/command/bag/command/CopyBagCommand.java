package com.e205.base.member.command.bag.command;

import lombok.Builder;

@Builder
public record CopyBagCommand(
    Integer memberId,
    Integer bagsId,
    String newBagName
) {

}
