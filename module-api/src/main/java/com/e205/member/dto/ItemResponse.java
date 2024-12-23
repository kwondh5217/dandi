package com.e205.member.dto;

import com.e205.base.member.command.bag.payload.BagItemPayload;
import com.e205.base.member.command.item.payload.ItemPayload;
import lombok.Builder;

@Builder
public record ItemResponse(
    Integer itemId,
    Byte itemOrder,
    String name,
    String emoticon,
    Byte colorKey
) {
  public static ItemResponse from(BagItemPayload bagItem, ItemPayload itemPayload) {
    return ItemResponse.builder()
        .itemId(bagItem.itemId())
        .itemOrder(bagItem.itemOrder())
        .name(itemPayload.name())
        .emoticon(itemPayload.emoticon())
        .colorKey(itemPayload.colorKey())
        .build();
  }
  public static ItemResponse from(ItemPayload itemPayload) {
    return ItemResponse.builder()
        .itemId(itemPayload.id())
        .itemOrder(itemPayload.itemOrder())
        .name(itemPayload.name())
        .emoticon(itemPayload.emoticon())
        .colorKey(itemPayload.colorKey())
        .build();
  }
}
