package com.e205.domain.item.service;

import com.e205.domain.item.dto.CreateItemCommand;
import com.e205.domain.item.dto.ItemOrder;
import com.e205.domain.item.dto.UpdateItemCommand;
import com.e205.domain.item.dto.UpdateItemOrderCommand;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandServiceDefault implements ItemCommandService {

  private final ItemRepository itemRepository;

  @Override
  public void save(CreateItemCommand createItemCommand) {
    Integer memberId = createItemCommand.memberId();
    byte maxItemOrder = itemRepository.findMaxItemOrderByMemberId(memberId);

    Item item = Item.builder()
        .name(createItemCommand.name())
        .emoticon(createItemCommand.emoticon())
        .colorKey(createItemCommand.colorKey())
        .memberId(memberId)
        .itemOrder(maxItemOrder)
        .build();

    itemRepository.save(item);
  }

  @Override
  public void update(UpdateItemCommand updateCommand) {
    // TODO: <홍성우> Exception 상세화
    Item item = itemRepository.findById(updateCommand.currentMemberId())
        .orElseThrow(RuntimeException::new);

    item.updateName(updateCommand.name());
    item.updateEmoticon(updateCommand.emoticon());
    item.updateColorKey(updateCommand.colorKey());
  }

  @Override
  public void updateItemOrder(UpdateItemOrderCommand updateItemOrderCommand) {
    Integer currentUserId = updateItemOrderCommand.memberId();

    List<Item> userItems = itemRepository.findAllByMemberId(currentUserId);

    Map<Integer, Item> itemMap = userItems.stream()
        .collect(Collectors.toMap(Item::getId, Function.identity()));

    for (ItemOrder itemOrder : updateItemOrderCommand.items()) {
      Item item = itemMap.get(itemOrder.itemId());
      item.updateOrder(itemOrder.order());
    }
  }
}