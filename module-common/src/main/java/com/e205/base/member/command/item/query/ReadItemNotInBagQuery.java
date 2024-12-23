package com.e205.base.member.command.item.query;

public record ReadItemNotInBagQuery(
    Integer memberId,
    Integer bagId
) {

}
