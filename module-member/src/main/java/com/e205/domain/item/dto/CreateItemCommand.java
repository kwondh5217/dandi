package com.e205.domain.item.dto;

public record CreateItemCommand(Integer bagId, String emoticon, String name,
                                byte colorKey, Integer memberId) {

}
