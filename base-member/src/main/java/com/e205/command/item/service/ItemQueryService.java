package com.e205.command.item.service;

import com.e205.command.bag.query.ReadAllItemQuery;
import com.e205.command.item.query.ReadItemNotInBagQuery;
import com.e205.command.item.payload.ItemPayload;
import java.util.List;

public interface ItemQueryService {

  List<ItemPayload> readAllItems(ReadAllItemQuery readAllItemQuery);

  List<ItemPayload> readItemsNotInBag(ReadItemNotInBagQuery readItemNotInBagQuery);
}