package com.e205.domain.item.dto;

public record UpdateItemCommand(Integer currentMemberId, int itemId, String emoticon, String name, byte colorKey) {

}
