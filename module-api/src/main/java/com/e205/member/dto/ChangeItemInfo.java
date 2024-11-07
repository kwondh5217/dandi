package com.e205.member.dto;

import com.e205.command.item.command.UpdateItemCommand;

public record ChangeItemInfo(
    String emoticon,
    String name,
    Byte colorKey
) {

  public UpdateItemCommand toCommand(Integer memberId, Integer itemId) {
    return UpdateItemCommand.builder()
        .itemId(itemId)
        .memberId(memberId)
        .emoticon(emoticon)
        .name(name)
        .colorKey(colorKey)
        .build();
  }
}
