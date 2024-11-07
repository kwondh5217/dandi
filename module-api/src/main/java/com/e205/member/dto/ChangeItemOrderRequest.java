package com.e205.member.dto;

public record ChangeItemOrderRequest(
    Integer itemId,
    Integer orderId
) {

}
