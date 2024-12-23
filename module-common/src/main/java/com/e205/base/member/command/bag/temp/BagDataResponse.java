package com.e205.base.member.command.bag.temp;

import lombok.Builder;

@Builder
public record BagDataResponse(
    Integer id,
    Byte bagOrder,
    char enabled
) {

}