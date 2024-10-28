package com.e205.domain.item.service;

import com.e205.domain.item.dto.ItemDataResponse;
import java.util.List;

public interface ItemQueryService {

  List<ItemDataResponse> readAllItems(Integer memberId);
}