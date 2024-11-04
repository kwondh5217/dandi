package com.e205.item.dto;

import com.e205.FoundItemType;

public record FoundItemResponse(
    Integer id,
    Integer memberId,
    Point foundLocation,
    String description,
    String savePoint,
    FoundItemType type,
    String image
) {

}
