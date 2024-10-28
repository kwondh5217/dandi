package com.e205.domain.item.service;

import com.e205.domain.item.dto.ItemDataResponse;
import com.e205.domain.item.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.e205.domain.item.entity.Item;

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
}