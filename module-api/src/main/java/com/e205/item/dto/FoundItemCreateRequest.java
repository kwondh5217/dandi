package com.e205.item.dto;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import com.e205.geo.dto.Point;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FoundItemCreateRequest(
    FoundItemType category,
    Point foundLocation,
    String image,
    LocalDateTime foundAt,
    String storageDesc,
    String itemDesc
) {
  public FoundItemSaveCommand toCommand(Integer memberId, String address) {
    return FoundItemSaveCommand.builder()
        .foundAt(foundAt)
        .itemDesc(itemDesc)
        .storageDesc(storageDesc)
        .type(category)
        .location(foundLocation.toGeoPoint())
        .address(address)
        .memberId(memberId)
        .image(image)
        .build();
  }
}
