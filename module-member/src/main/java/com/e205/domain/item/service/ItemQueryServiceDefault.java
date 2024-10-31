package com.e205.domain.item.service;

import com.e205.command.item.payload.ItemPayload;
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
  public List<ItemPayload> readAllItems(Integer memberId) {
    return itemRepository.findAllByMemberId(memberId)
        .stream()
        .map(Item::toPayload)
        .toList();
  }

  @Override
  public List<ItemPayload> readItemsNotInBag(Integer memberId, Integer bagId) {
    return itemRepository.findItemsNotInBag(memberId, bagId)
        .stream()
        .map(Item::toPayload)
        .toList();
  }
}