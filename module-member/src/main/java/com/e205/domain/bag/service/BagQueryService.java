package com.e205.domain.bag.service;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.item.payload.ItemPayload;
import java.util.List;

public interface BagQueryService {

  List<BagPayload> readAllBags(Integer memberId);

  List<BagItemPayload> readAllBagItemsByBagId(Integer bagId);

  List<ItemPayload> readAllByItemIds(List<Integer> itemIds);
}
