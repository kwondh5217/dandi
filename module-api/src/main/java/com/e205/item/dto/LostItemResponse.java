package com.e205.item.dto;

import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record LostItemResponse(
    Integer id,
    Integer memberId,
    String situationDescription,
    String itemDescription,
    List<String> images,
    LocalDateTime lostAt
) {

  public static LostItemResponse from(LostItemPayload payload, List<String> images) {
    return LostItemResponse.builder()
        .id(payload.id())
        .memberId(payload.memberId())
        .situationDescription(payload.situationDescription())
        .itemDescription(payload.itemDescription())
        .images(images)
        .lostAt(payload.lostAt())
        .build();
  }
}
