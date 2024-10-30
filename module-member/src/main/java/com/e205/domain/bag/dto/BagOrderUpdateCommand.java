package com.e205.domain.bag.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record BagOrderUpdateCommand(
    Integer memberId,
    List<BagOrder> bags
) {

}