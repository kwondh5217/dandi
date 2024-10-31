package com.e205.command.bag.command;

import java.util.List;

public record BagItemOrderUpdateCommand(
    Integer memberId,
    Integer bagId,
    List<BagItemOrderCommand> items) {

}
