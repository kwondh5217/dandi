package com.e205.base.member.command.bag.command;

import java.util.List;

public record BagItemOrderUpdateCommand(
    Integer memberId,
    Integer bagId,
    List<BagItemOrderCommand> items) {

}
