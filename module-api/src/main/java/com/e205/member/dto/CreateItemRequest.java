package com.e205.member.dto;

import com.e205.command.item.command.CreateItemCommand;

public record CreateItemRequest(
    Integer bagId,
    String emoticon,
    String name,
    Byte colorKey
) {

  public CreateItemCommand toCommand(Integer memberId) {
    return CreateItemCommand.builder()
        .bagId(bagId)
        .emoticon(emoticon)
        .name(name)
        .colorKey(colorKey)
        .memberId(memberId)
        .build();
  }
}
