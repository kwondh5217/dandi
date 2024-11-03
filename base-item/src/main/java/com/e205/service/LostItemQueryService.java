package com.e205.service;

import com.e205.payload.LostItemPayload;
import com.e205.query.LostItemQuery;

public interface LostItemQueryService {

  LostItemPayload find(LostItemQuery query);
}
