package com.e205.service;

import com.e205.payload.LostItemPayload;
import com.e205.query.LostItemListQuery;
import com.e205.query.LostItemQuery;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface LostItemQueryService {

  LostItemPayload find(LostItemQuery query);
}
