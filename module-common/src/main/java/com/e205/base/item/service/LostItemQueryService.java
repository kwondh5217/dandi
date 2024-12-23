package com.e205.base.item.service;

import com.e205.base.item.payload.ItemImagePayload;
import com.e205.base.item.payload.LostItemPayload;
import com.e205.base.item.query.LostItemListQuery;
import com.e205.base.item.query.LostItemQuery;
import java.util.List;

public interface LostItemQueryService {

  LostItemPayload find(LostItemQuery query);

  List<LostItemPayload> find(LostItemListQuery query);

  List<ItemImagePayload> findImages(Integer lostId);

  boolean isCreatable(Integer memberId);
}
