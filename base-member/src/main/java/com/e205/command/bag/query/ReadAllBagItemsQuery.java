package com.e205.command.bag.query;

public record ReadAllBagItemsQuery(
    Integer memberId,
    Integer bagId
) {

}
