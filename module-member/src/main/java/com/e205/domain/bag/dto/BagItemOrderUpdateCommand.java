package com.e205.domain.bag.dto;

import java.util.List;

public record BagItemOrderUpdateCommand(
    Integer memberId,
    Integer bagId,
    List<BagItemOrder> items) {

}
