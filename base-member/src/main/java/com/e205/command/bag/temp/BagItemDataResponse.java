package com.e205.command.bag.temp;

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