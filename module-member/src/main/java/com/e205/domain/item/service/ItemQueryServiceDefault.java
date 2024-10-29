package com.e205.domain.item.service;

import com.e205.domain.item.dto.ItemDataResponse;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemQueryServiceDefault implements ItemQueryService {

  private final ItemRepository itemRepository;

  @Override
  public List<ItemDataResponse> readAllItems(Integer memberId) {
    List<Item> items = itemRepository.findAllByMemberId(memberId);

    return items.stream()
        .map(Item::of)
        .toList();
  }

  @Override
  public List<ItemDataResponse> readItemsNotInBag(Integer memberId, Integer bagId) {
    List<Item> itemsNotInBag = itemRepository.findItemsNotInBag(memberId, bagId);

    return itemsNotInBag.stream()
        .map(Item::of)
        .toList();
  }
}