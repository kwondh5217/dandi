package com.e205.command.bag.temp;

import lombok.Builder;

@Builder
public record BagDataResponse(
    Integer id,
    Byte bagOrder,
    char enabled
) {

}