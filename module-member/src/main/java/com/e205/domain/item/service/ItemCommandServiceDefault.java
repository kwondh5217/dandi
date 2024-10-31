package com.e205.domain.item.service;

import com.e205.command.item.command.CreateItemCommand;
import com.e205.command.item.command.DeleteItemCommand;
import com.e205.command.item.command.UpdateItemCommand;
import com.e205.command.item.command.UpdateItemOrderCommand;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandServiceDefault implements ItemCommandService {

  private final ItemRepository itemRepository;
  private final BagItemRepository bagItemRepository;
  private static final int MAX_ITEM_COUNT = 50;
  private static final int MAX_BAG_ITEM_COUNT = 20;

  @Override
  public void save(CreateItemCommand createItemCommand) {
    Integer memberId = createItemCommand.memberId();
    Integer bagId = createItemCommand.bagId();

    boolean isDuplicateName = itemRepository.existsByNameAndMemberId(createItemCommand.name(), memberId);
    if (isDuplicateName) {
      throw new RuntimeException();
    }

    List<Item> userItems = itemRepository.findAllByMemberId(memberId);
    if (userItems.size() >= MAX_ITEM_COUNT) {
      throw new RuntimeException();
    }

    List<BagItem> bagItems = bagItemRepository.findAllByBagId(bagId);
    if (bagItems.size() >= MAX_BAG_ITEM_COUNT) {
      throw new RuntimeException();
    }

    // 새로운 아이템 순서 설정
    byte maxItemOrder = (byte) (userItems.stream()
        .mapToInt(Item::getItemOrder)
        .max()
        .orElse(0) + 1);

    Item item = Item.builder()
        .name(createItemCommand.name())
        .emoticon(createItemCommand.emoticon())
        .colorKey(createItemCommand.colorKey())
        .memberId(memberId)
        .itemOrder(maxItemOrder)
        .build();

    itemRepository.save(item);

    BagItem bagItem = BagItem.builder()
        .bagId(bagId)
        .itemId(item.getId())
        .itemOrder(maxItemOrder)
        .build();
    bagItemRepository.save(bagItem);
  }

  @Override
  public void update(UpdateItemCommand updateCommand) {
    Item item = itemRepository.findById(updateCommand.itemId())
        .orElseThrow(RuntimeException::new);

    if(item.getMemberId() != updateCommand.memberId()) {
      throw new RuntimeException();
    }

    boolean isDuplicateName = itemRepository.existsByNameAndMemberIdAndIdNot(
        updateCommand.name(), item.getMemberId(), item.getId());

    if (isDuplicateName) {
      throw new RuntimeException();
    }

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

    updateItemOrderCommand.items().forEach(itemOrder -> {
      Item item = itemMap.get(itemOrder.itemId());
      item.updateOrder(itemOrder.order());
    });
  }

  @Override
  public void delete(DeleteItemCommand deleteItemCommand) {
    Integer memberId = deleteItemCommand.memberId();
    Integer itemId = deleteItemCommand.itemId();

    // TODO: <홍성우> Exception 상세화
    Item item = itemRepository.findByIdAndMemberId(itemId, memberId)
        .orElseThrow(RuntimeException::new);

    List<BagItem> bagItems = bagItemRepository.findAllByItemId(itemId);
    bagItemRepository.deleteAll(bagItems);

    itemRepository.delete(item);
  }
}