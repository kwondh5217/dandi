package com.e205.dto;

import com.e205.entity.LostItem;
import java.util.List;
import org.locationtech.jts.geom.LineString;
import org.springframework.core.io.Resource;

public record LostItemSaveCommand(
    Integer lostMemberId,
    List<Resource> images,
    LineString route,
    String situationDesc,
    String itemDesc
) {
  public LostItem toEntity() {
    return LostItem.builder()
        .memberId(lostMemberId)
        .route(route)
        .situationDescription(situationDesc)
        .itemDescription(itemDesc)
        .build();
  }
}
