package com.e205.base.member.command.bag.command;

import lombok.Builder;

@Builder
public record BagDeleteCommand(
    Integer memberId,
    Integer bagId,
    Integer memberBagId
) {

}
