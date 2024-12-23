package com.e205.base.member.command.bag.command;

import java.util.List;
import lombok.Builder;

@Builder
public record BagOrderUpdateCommand(
    Integer memberId,
    List<BagOrderCommand> bags
) {

}