package com.e205.base.member.command.item.command;

import lombok.Builder;

@Builder
public record CreateItemCommand(Integer bagId, String emoticon, String name,
                                byte colorKey, Integer memberId
) {

}
