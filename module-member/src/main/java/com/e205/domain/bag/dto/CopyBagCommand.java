package com.e205.domain.bag.dto;

public record CopyBagCommand(
    Integer memberId,
    Integer bagsId,
    String newBagName
) {

}
