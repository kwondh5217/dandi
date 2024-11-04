package com.e205.service;

import com.e205.payload.FoundItemPayload;
import com.e205.query.FoundItemQuery;

public interface FoundItemQueryService {

  FoundItemPayload find(FoundItemQuery query);
}
