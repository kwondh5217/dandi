package com.e205.item.dto;

import com.e205.payload.LostItemPayload;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record LostItemResponse(
    String situationDescription,
    String itemDescription,
    List<String> images,
    LocalDateTime lostAt
) {

  public static LostItemResponse from(LostItemPayload payload, List<String> images) {
    return LostItemResponse.builder()
        .situationDescription(payload.situationDescription())
        .itemDescription(payload.itemDescription())
        .images(images)
        // TODO <fosong98> 분실물 생성 시 잃어버린 시간도 받아야 한다.
//        .lostAt(payload.lostAt)
        .build();
  }
}
