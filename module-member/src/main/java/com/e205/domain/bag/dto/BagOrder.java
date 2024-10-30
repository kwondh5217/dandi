package com.e205.domain.bag.dto;

import lombok.Builder;

@Builder
public record BagOrder(
    Integer bagId,
    Byte order
) {

}
