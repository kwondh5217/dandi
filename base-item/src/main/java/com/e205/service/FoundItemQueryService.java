package com.e205.service;

import com.e205.payload.FoundItemPayload;
import com.e205.payload.ItemImagePayload;
import com.e205.query.FoundItemListQuery;
import com.e205.query.FoundItemQuery;
import java.util.List;

public interface FoundItemQueryService {

  FoundItemPayload find(FoundItemQuery query);

  List<FoundItemPayload> find(FoundItemListQuery query);

  ItemImagePayload findFoundItemImage(Integer foundId);
}
