package com.e205.domain.bag.dto;

import lombok.Builder;

@Builder
public record BagDataResponse(
    Integer id,
    Byte bagOrder,
    char enabled
) {

}