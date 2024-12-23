package com.e205.base.member.command.bag.command;

import java.util.List;

public record RemoveItemsInBagCommand(
    Integer bagId,
    List<Integer> itemIds,
    Integer memberId
) {
}
