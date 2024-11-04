package com.e205.command.bag.query;

import java.util.List;

public record ReadAllItemInfoQuery(
    List<Integer> itemIds
) {

}
