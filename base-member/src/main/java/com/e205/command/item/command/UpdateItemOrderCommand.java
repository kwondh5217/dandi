package com.e205.command.item.command;

import java.util.List;
import lombok.Builder;

@Builder
public record UpdateItemOrderCommand(
    Integer memberId,
    List<ItemOrderCommand> items
) {

}
