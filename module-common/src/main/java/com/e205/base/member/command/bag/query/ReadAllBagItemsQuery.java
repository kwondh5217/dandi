package com.e205.base.member.command.bag.query;

public record ReadAllBagItemsQuery(
    Integer memberId,
    Integer bagId
) {

}
