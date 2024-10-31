package com.e205.command.item.command;

import java.util.List;

public record UpdateItemOrderCommand(Integer memberId, List<ItemOrderCommand> items) {

}
