package com.e205.domain.bag.service;

import com.e205.domain.bag.dto.BagDataResponse;
import com.e205.domain.bag.dto.BagItemDataResponse;
import java.util.List;

public interface BagQueryService {

  List<BagDataResponse> readAllBags(Integer memberId);

  List<BagItemDataResponse> readAllItemsByBagId(Integer bagId);

}
