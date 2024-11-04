package com.e205.command.bag.query;

public record ReadItemNotInBagQuery(
    Integer memberId,
    Integer bagId
) {

}
