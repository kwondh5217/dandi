package com.e205.item.dto;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import java.time.LocalDateTime;
import org.springframework.core.io.Resource;

public record FoundItemCreateRequest(
    FoundItemType category,
    Point foundLocation,
    LocalDateTime foundAt,
    String storageDesc,
    String itemDesc
) {
  public FoundItemSaveCommand toCommand(Integer memberId, Resource image) {
    return FoundItemSaveCommand.builder()
        .foundAt(foundAt)
        .itemDesc(itemDesc)
        .storageDesc(storageDesc)
        .type(category)
        .location(foundLocation.toGeoPoint())
        .memberId(memberId)
        .image(image)
        .build();
  }
}
