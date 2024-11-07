package com.e205.command.item.command;

import lombok.Builder;

@Builder
public record CreateItemCommand(Integer bagId, String emoticon, String name,
                                byte colorKey, Integer memberId
) {

}
