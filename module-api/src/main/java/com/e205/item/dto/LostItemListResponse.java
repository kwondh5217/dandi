package com.e205.item.dto;

import java.util.List;

public record LostItemListResponse(
    List<LostItemResponse> items
) {

  public static LostItemListResponse from(List<LostItemResponse> items) {
    return new LostItemListResponse(items);
  }
}
