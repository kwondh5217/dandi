package com.e205.base.member.command.item.service;

import com.e205.base.member.command.bag.query.ReadAllItemQuery;
import com.e205.base.member.command.item.query.ReadItemNotInBagQuery;
import com.e205.base.member.command.item.payload.ItemPayload;
import java.util.List;

public interface ItemQueryService {

  List<ItemPayload> readAllItems(ReadAllItemQuery readAllItemQuery);

  List<ItemPayload> readItemsNotInBag(ReadItemNotInBagQuery readItemNotInBagQuery);
}