package com.e205.domain.item.dto;

public record UpdateItemCommand(int memberId, int itemId, String emoticon, String name, byte colorKey) {

}
