package com.e205.domain.item.service;

import com.e205.command.bag.query.ReadAllItemQuery;
import com.e205.command.item.payload.ItemPayload;
import com.e205.command.item.query.ReadItemNotInBagQuery;
import com.e205.command.item.service.ItemQueryService;
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
  public List<ItemPayload> readAllItems(ReadAllItemQuery readAllItemQuery) {
    return itemRepository.findAllByMemberId(readAllItemQuery.memberId())
        .stream()
        .map(Item::toPayload)
        .toList();
  }

  @Override
  public List<ItemPayload> readItemsNotInBag(ReadItemNotInBagQuery readItemNotInBagQuery) {
    return itemRepository.findItemsNotInBag(readItemNotInBagQuery.memberId(), readItemNotInBagQuery.bagId())
        .stream()
        .map(Item::toPayload)
        .toList();
  }
}