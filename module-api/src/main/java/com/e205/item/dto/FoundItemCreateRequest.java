package com.e205.item.dto;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.core.io.Resource;

@Builder
public record FoundItemCreateRequest(
    FoundItemType category,
    Point foundLocation,
    String image,
    LocalDateTime foundAt,
    String storageDesc,
    String itemDesc
) {
  public FoundItemSaveCommand toCommand(Integer memberId) {
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
