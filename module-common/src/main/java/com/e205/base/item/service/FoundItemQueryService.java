package com.e205.base.item.service;

import com.e205.base.item.payload.ItemImagePayload;
import com.e205.base.item.query.FoundItemListQuery;
import com.e205.base.item.payload.FoundItemPayload;
import com.e205.base.item.query.FoundItemQuery;
import java.util.List;

public interface FoundItemQueryService {

  FoundItemPayload find(FoundItemQuery query);

  List<FoundItemPayload> find(FoundItemListQuery query);

  ItemImagePayload findFoundItemImage(Integer foundId);

  List<FoundItemPayload> findReadable(int memberId);
}
