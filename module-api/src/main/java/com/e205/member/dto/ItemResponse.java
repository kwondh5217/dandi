package com.e205.member.dto;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.item.payload.ItemPayload;
import java.time.LocalDateTime;
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
}
