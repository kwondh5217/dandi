package com.e205.domain.bag.service;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.query.ReadAllBagItemsQuery;
import com.e205.command.bag.query.ReadAllBagsQuery;
import com.e205.command.bag.query.ReadAllItemInfoQuery;
import com.e205.command.bag.service.BagQueryService;
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
  public List<BagPayload> readAllBags(ReadAllBagsQuery readAllBagsQuery) {
    return bagRepository.findAllByMemberId(readAllBagsQuery.memberId())
        .stream()
        .map(Bag::toPayload)
        .toList();
  }

  @Override
  public List<BagItemPayload> readAllBagItemsByBagId(ReadAllBagItemsQuery readAllBagItemsQuery) {
    if (!bagRepository.existsByIdAndMemberId(readAllBagItemsQuery.bagId(),
        readAllBagItemsQuery.memberId())) {
      throw new IllegalArgumentException("해당 가방은 이 회원의 소유가 아닙니다.");
    }

    return bagItemRepository.findAllByBagId(readAllBagItemsQuery.bagId()).stream()
        .map(BagItem::toPayload)
        .toList();
  }

  @Override
  public List<ItemPayload> readAllByItemIds(ReadAllItemInfoQuery readAllItemInfoQuery) {
    return itemRepository.findAllById(readAllItemInfoQuery.itemIds())
        .stream()
        .map(Item::toPayload)
        .toList();
  }
}