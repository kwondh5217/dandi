package com.e205.domain.bag.dto;

public record BagNameUpdateCommand(
    Integer memberId,
    Integer bagId,
    String name
) {

}

