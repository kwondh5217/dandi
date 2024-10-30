package com.e205.domain.bag.dto;

public record SelectBagCommand(
    Integer myBagId,
    Integer targetBagId,
    Integer memberId
) {

}
