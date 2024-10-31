package com.e205.domain.bag.service;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.item.payload.ItemPayload;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BagQueryServiceDefault implements BagQueryService {

  private final BagRepository bagRepository;
  private final BagItemRepository bagItemRepository;
  private final ItemRepository itemRepository;

  @Override
  public List<BagPayload> readAllBags(Integer memberId) {
    return bagRepository.findAllByMemberId(memberId)
        .stream()
        .map(Bag::toPayload)
        .toList();
  }

  @Override
  public List<BagItemPayload> readAllBagItemsByBagId(Integer bagId) {
    return bagItemRepository.findAllByBagId(bagId)
        .stream()
        .map(BagItem::toPayload)
        .toList();
  }

  @Override
  public List<ItemPayload> readAllByItemIds(List<Integer> itemIds) {
    return itemRepository.findAllById(itemIds)
        .stream()
        .map(Item::toPayload)
        .toList();
  }
}