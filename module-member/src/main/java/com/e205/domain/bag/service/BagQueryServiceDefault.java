package com.e205.domain.bag.service;

import com.e205.domain.bag.dto.BagDataResponse;
import com.e205.domain.bag.dto.BagItemDataResponse;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BagQueryServiceDefault implements BagQueryService {

  private final BagRepository bagRepository;
  private final BagItemRepository bagItemRepository;

  // TODO: <홍성우> Entity 반환으로 변경
  @Override
  public List<BagDataResponse> readAllBags(Integer memberId) {
    List<Bag> bags = bagRepository.findAllByMemberId(memberId);

    return bags.stream()
        .map(Bag::of)
        .toList();
  }

  @Override
  public List<BagItemDataResponse> readAllItemsByBagId(Integer bagId) {
    return bagItemRepository.findAllItemsByBagId(bagId);
  }
}