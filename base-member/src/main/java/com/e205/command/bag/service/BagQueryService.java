package com.e205.command.bag.service;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.query.ReadAllBagItemsQuery;
import com.e205.command.bag.query.ReadAllBagsQuery;
import com.e205.command.bag.query.ReadAllItemInfoQuery;
import com.e205.command.item.payload.ItemPayload;
import java.util.List;

public interface BagQueryService {

  List<BagPayload> readAllBags(ReadAllBagsQuery readAllBagsQuery);

  List<BagItemPayload> readAllBagItemsByBagId(ReadAllBagItemsQuery readAllBagItemsQuery);

  List<ItemPayload> readAllByItemIds(ReadAllItemInfoQuery readAllItemInfoQuery);
}
