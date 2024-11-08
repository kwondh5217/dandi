package com.e205.item.dto;

import java.util.List;

public record FoundItemListResponse(
    List<FoundItemResponse> items
) {

  public static FoundItemListResponse from(List<FoundItemResponse> items) {
    return new FoundItemListResponse(items);
  }
}
