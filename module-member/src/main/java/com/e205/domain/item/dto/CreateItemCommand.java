package com.e205.domain.item.dto;

public record CreateItemCommand(Integer currentMemberId, String emoticon, String name,
                                byte colorKey, Integer memberId) {

}
