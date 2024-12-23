package com.e205.item.dto;

import com.e205.base.item.FoundItemType;
import com.e205.geo.dto.Point;
import com.e205.base.item.payload.FoundItemPayload;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FoundItemResponse(
    Integer id,
    Integer memberId,
    Point foundLocation,
    String description,
    String savePoint,
    FoundItemType type,
    LocalDateTime foundAt,
    String address,
    String image
) {

  public static FoundItemResponse from(FoundItemPayload payload, String image) {
    return builder()
        .id(payload.id())
        .memberId(payload.memberId())
        .foundLocation(new Point(payload.lat(), payload.lon()))
        .description(payload.description())
        .savePoint(payload.savePlace())
        .foundAt(payload.foundAt())
        .type(payload.type())
        .address(payload.address())
        .image(image)
        .build();
  }
}
