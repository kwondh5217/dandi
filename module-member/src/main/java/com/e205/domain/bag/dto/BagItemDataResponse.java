package com.e205.domain.bag.dto;

import lombok.Builder;

@Builder
public record BagItemDataResponse(
    Integer itemId,
    Byte itemOrder,
    String name,
    String emoticon,
    Byte colorKey
) {

}