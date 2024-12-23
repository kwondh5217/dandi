package com.e205.base.member.command.bag.query;

import java.util.List;

public record ReadAllItemInfoQuery(
    List<Integer> itemIds
) {

}
