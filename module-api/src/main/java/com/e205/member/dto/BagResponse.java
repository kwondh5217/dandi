package com.e205.member.dto;

import com.e205.command.bag.payload.BagPayload;
import lombok.Builder;

@Builder
public record BagResponse(
    Integer id,
    String name,
    int bagOrder
) {
  public static BagResponse from(BagPayload payload) {
    return BagResponse.builder()
        .id(payload.id())
        .name(payload.name())
        .bagOrder(payload.bagOrder())
        .build();
  }
}