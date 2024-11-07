package com.e205.command.item.query;

public record ReadItemNotInBagQuery(
    Integer memberId,
    Integer bagId
) {

}
