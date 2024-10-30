package com.e205.domain.bag.dto;

public record CreateBagCommand(
    Integer memberId,
    String name
) {

}

