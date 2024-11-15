package com.e205.domain.item.service;

import com.e205.command.bag.event.BagItemAddEvent;
import com.e205.command.bag.event.BagItemChangedEvent;
import com.e205.command.bag.event.BagItemDeleteEvent;
import com.e205.command.item.command.CreateItemCommand;
import com.e205.command.item.command.DeleteItemCommand;
import com.e205.command.item.command.UpdateItemCommand;
import com.e205.command.item.command.UpdateItemOrderCommand;
import com.e205.command.item.payload.ItemPayload;
import com.e205.command.item.service.ItemCommandService;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.exception.MemberError;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import com.e205.events.EventPublisher;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommandServiceDefault implements ItemCommandService {

  private static final int MAX_ITEM_COUNT = 50;
  private static final int MAX_BAG_ITEM_COUNT = 20;
  private final ItemRepository itemRepository;
  private final BagItemRepository bagItemRepository;
  private final EventPublisher eventPublisher;
  private final BagRepository bagRepository;

  @Override
  public void save(CreateItemCommand createItemCommand) {
    Integer memberId = createItemCommand.memberId();
    Integer bagId = createItemCommand.bagId();

    Bag bag = bagRepository.findById(bagId)
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);
    if (!bag.getMemberId().equals(memberId)) {
      throw MemberError.BAG_NOT_OWNED_BY_USER.getGlobalException();
    }

    boolean isDuplicateName = itemRepository.existsByNameAndMemberId(createItemCommand.name(),
        memberId);
    if (isDuplicateName) {
      throw MemberError.ITEM_NAME_ALREADY_EXISTS.getGlobalException();
    }

    List<Item> userItems = itemRepository.findAllByMemberId(memberId);
    if (userItems.size() >= MAX_ITEM_COUNT) {
      throw MemberError.ITEM_COUNT_EXCEEDED.getGlobalException();
    }

    List<BagItem> bagItems = bagItemRepository.findAllByBagId(bagId);
    if (bagItems.size() >= MAX_BAG_ITEM_COUNT) {
      throw MemberError.MAX_BAG_ITEM_COUNT_EXCEEDED.getGlobalException();
    }

    byte maxItemOrder = (byte) (bagItems.stream()
        .mapToInt(BagItem::getItemOrder)
        .max()
        .orElse(0) + 1);

    Item item = Item.builder()
        .name(createItemCommand.name())
        .emoticon(createItemCommand.emoticon())
        .colorKey(createItemCommand.colorKey())
        .memberId(memberId)
        .itemOrder(maxItemOrder)
        .createdAt(LocalDateTime.now())
        .build();
    itemRepository.save(item);

    BagItem bagItem = BagItem.builder()
        .bagId(bagId)
        .itemId(item.getId())
        .itemOrder(maxItemOrder)
        .createdAt(LocalDateTime.now())
        .build();
    bagItemRepository.save(bagItem);

    eventPublisher.publishAtLeastOnce(new BagItemAddEvent(item.toPayload()));
  }

  @Override
  public void update(UpdateItemCommand updateCommand) {
    Item item = itemRepository.findById(updateCommand.itemId())
        .orElseThrow(MemberError.ITEM_NOT_FOUND::getGlobalException);

    ItemPayload previousItemPayload = item.toPayload();

    if (item.getMemberId() != updateCommand.memberId()) {
      throw MemberError.ITEM_NOT_FOUND.getGlobalException();
    }

    boolean isDuplicateName = itemRepository.existsByNameAndMemberIdAndIdNot(
        updateCommand.name(), item.getMemberId(), item.getId());

    if (isDuplicateName) {
      throw MemberError.ITEM_NAME_ALREADY_EXISTS.getGlobalException();
    }

    item.updateName(updateCommand.name());
    item.updateEmoticon(updateCommand.emoticon());
    item.updateColorKey(updateCommand.colorKey());

    eventPublisher.publishAtLeastOnce(new BagItemChangedEvent(previousItemPayload, item.toPayload()));
  }

  @Override
  public void updateItemOrder(UpdateItemOrderCommand updateItemOrderCommand) {
    Integer currentUserId = updateItemOrderCommand.memberId();

    List<Item> userItems = itemRepository.findAllByMemberId(currentUserId);

    Map<Integer, Item> itemMap = userItems.stream()
        .collect(Collectors.toMap(Item::getId, Function.identity()));

    updateItemOrderCommand.items().forEach(itemOrder -> {
      Item item = itemMap.get(itemOrder.itemId());
      if (item != null) {
        item.updateOrder(itemOrder.order());
      }
    });
  }

  @Override
  public void delete(DeleteItemCommand deleteItemCommand) {
    Integer memberId = deleteItemCommand.memberId();
    Integer itemId = deleteItemCommand.itemId();

    Item item = itemRepository.findByIdAndMemberId(itemId, memberId)
        .orElseThrow(MemberError.ITEM_NOT_FOUND::getGlobalException);

    List<BagItem> bagItems = bagItemRepository.findAllByItemId(itemId);
    bagItemRepository.deleteAll(bagItems);

    itemRepository.delete(item);
    eventPublisher.publishAtLeastOnce(new BagItemDeleteEvent(item.toPayload()));
  }
}