package com.e205.domain.item.service;

import com.e205.command.item.payload.ItemPayload;
import java.util.List;

public interface ItemQueryService {

  List<ItemPayload> readAllItems(Integer memberId);

  List<ItemPayload> readItemsNotInBag(Integer memberId, Integer bagId);
}