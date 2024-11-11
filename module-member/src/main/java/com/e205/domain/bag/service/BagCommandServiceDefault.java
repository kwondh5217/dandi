package com.e205.domain.bag.service;

import com.e205.command.bag.command.AddItemsToBagCommand;
import com.e205.command.bag.command.BagDeleteCommand;
import com.e205.command.bag.command.BagItemDeleteCommand;
import com.e205.command.bag.command.BagItemOrderUpdateCommand;
import com.e205.command.bag.command.BagNameUpdateCommand;
import com.e205.command.bag.command.BagOrderUpdateCommand;
import com.e205.command.bag.command.CopyBagCommand;
import com.e205.command.bag.command.CreateBagCommand;
import com.e205.command.bag.command.RemoveItemsInBagCommand;
import com.e205.command.bag.command.SelectBagCommand;
import com.e205.command.bag.event.BagChangedEvent;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.service.BagCommandService;
import com.e205.command.item.payload.ItemPayload;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import com.e205.events.EventPublisher;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class BagCommandServiceDefault implements BagCommandService {

  private static final int MAX_BAG_COUNT = 10;
  private final BagRepository bagRepository;
  private final BagItemRepository bagItemRepository;
  private final ItemRepository itemRepository;
  private final EventPublisher eventPublisher;

  @Override
  public void save(CreateBagCommand createBagCommand) {
    Integer memberId = createBagCommand.memberId();

    List<Bag> userBags = bagRepository.findAllByMemberId(memberId);
    if (userBags.size() >= MAX_BAG_COUNT) {
      // TODO: <홍성우> Exception 상세화
      throw new RuntimeException();
    }

    if (bagRepository.existsByMemberIdAndName(memberId, createBagCommand.name())) {
      // TODO: <홍성우> Exception 상세화
      throw new RuntimeException();
    }

    byte maxOrder = (byte) (userBags.stream()
        .mapToInt(Bag::getBagOrder)
        .max()
        .orElse(0) + 1);

    Bag bag = Bag.builder()
        .memberId(memberId)
        .name(createBagCommand.name())
        .enabled('Y')
        .bagOrder(maxOrder)
        .build();

    bagRepository.save(bag);
  }

  @Override
  public void updateBagOrder(BagOrderUpdateCommand bagOrderUpdateCommand) {
    List<Bag> bags = bagRepository.findAllByMemberId(bagOrderUpdateCommand.memberId());

    Map<Integer, Bag> bagMap = bags.stream()
        .collect(Collectors.toMap(Bag::getId, Function.identity()));

    bagOrderUpdateCommand.bags().forEach(order -> {
      Bag bag = bagMap.get(order.bagId());
      if (bag != null) {
        bag.updateBagOrder(order.order());
      }
    });
  }

  @Override
  public void updateBagName(BagNameUpdateCommand bagNameUpdateCommand) {
    // TODO: <홍성우> Exception 상세화
    Bag bag = bagRepository.findByIdAndMemberId(
            bagNameUpdateCommand.bagId(),
            bagNameUpdateCommand.memberId())
        .orElseThrow(RuntimeException::new);

    if (bagRepository.existsByMemberIdAndName(bag.getMemberId(), bagNameUpdateCommand.name())) {
      // TODO: <홍성우> Exception 상세화
      throw new RuntimeException();
    }
    bag.updateName(bagNameUpdateCommand.name());
  }

  @Override
  public void selectBag(SelectBagCommand selectBagCommand) {
    Integer memberId = selectBagCommand.memberId();
    // TODO: <홍성우> Exception 상세화
    Bag originalBag = bagRepository.findById(selectBagCommand.myBagId())
        .orElseThrow(RuntimeException::new);

    Bag targetBag = bagRepository.findById(selectBagCommand.targetBagId())
        .orElseThrow(RuntimeException::new);

    bagItemRepository.deleteAllByBagId(originalBag.getId());

    Integer targetBagId = targetBag.getId();
    List<BagItem> newBagItems = bagItemRepository.findAllByBagId(targetBagId).stream()
        .map(bagItem -> BagItem.builder()
            .bagId(originalBag.getId())
            .itemId(bagItem.getItemId())
            .itemOrder(bagItem.getItemOrder())
            .build())
        .toList();

    bagItemRepository.saveAll(newBagItems);
    eventPublisher.publicEvent(new BagChangedEvent(memberId, targetBagId));
  }

  private void convertAndPublishBagChangedEvent(List<BagItem> bagItems) {
    List<Integer> itemIds = bagItems.stream()
        .map(BagItem::getItemId)
        .toList();

    Map<Integer, Item> itemsById = itemRepository.findAllById(itemIds).stream()
        .collect(Collectors.toMap(Item::getId, Function.identity()));

    List<ItemPayload> itemPayloads = bagItems.stream()
        .map(bagItem -> Optional.ofNullable(itemsById.get(bagItem.getItemId()))
            .map(Item::toPayload)
            .orElseThrow(RuntimeException::new)
        )
        .toList();
  }

  @Override
  public void updateBagItemOrder(BagItemOrderUpdateCommand bagItemOrderUpdateCommand) {

    Bag bag = bagRepository.findById(bagItemOrderUpdateCommand.bagId())
        .orElseThrow(RuntimeException::new);

    if (!Objects.equals(bag.getMemberId(), bagItemOrderUpdateCommand.memberId())) {
      throw new RuntimeException();
    }

    List<BagItem> bagItems = bagItemRepository.findAllByBagId(bagItemOrderUpdateCommand.bagId());

    Map<Integer, BagItem> itemMap = bagItems.stream()
        .collect(Collectors.toMap(BagItem::getItemId, Function.identity()));

    bagItemOrderUpdateCommand.items().forEach(itemOrder -> {
      BagItem bagItem = itemMap.get(itemOrder.itemId());
      if (bagItem != null) {
        bagItem.updateOrder(itemOrder.order());
      }
    });
  }

  @Override
  public BagPayload copyBag(CopyBagCommand copyBagCommand) {
    Bag copyBag = bagRepository.findById(copyBagCommand.bagsId())
        .orElseThrow(RuntimeException::new);

    Integer memberId = copyBagCommand.memberId();
    if (bagRepository.findAllByMemberId(memberId).size() >= MAX_BAG_COUNT) {
      throw new RuntimeException();
    }

    Bag newBag = Bag.builder()
        .memberId(memberId)
        .name(copyBagCommand.newBagName())
        .bagOrder((byte) (bagRepository.findMaxBagOrderByMemberId(memberId) + 1))
        .enabled('Y')
        .build();
    bagRepository.save(newBag);

    List<BagItem> newBagItems = bagItemRepository.findAllByBagId(copyBag.getId()).stream()
        .map(bagItem -> BagItem.builder()
            .bagId(newBag.getId())
            .itemId(bagItem.getItemId())
            .itemOrder(bagItem.getItemOrder())
            .build())
        .toList();
    bagItemRepository.saveAll(newBagItems);

    return newBag.toPayload();
  }

  @Override
  public void delete(BagDeleteCommand command) {
    if (Objects.equals(command.memberBagId(), command.bagId())) {
      throw new IllegalArgumentException("기본 가방은 삭제할 수 없습니다.");
    }

    Bag targetBag = bagRepository.findById(command.bagId())
        .orElseThrow(() -> new IllegalArgumentException("삭제할 가방을 찾을 수 없습니다."));

    if (!Objects.equals(targetBag.getMemberId(), command.memberId())) {
      throw new IllegalArgumentException("삭제하려는 가방의 소유자가 아닙니다.");
    }

    bagItemRepository.deleteAllByBagId(targetBag.getId());
    bagRepository.delete(targetBag);
  }

  @Override
  public void deleteBagItem(BagItemDeleteCommand command) {

    Bag bag = bagRepository.findById(command.bagId())
        .orElseThrow(RuntimeException::new);

    if (!Objects.equals(bag.getMemberId(), command.memberId())) {
      throw new RuntimeException();
    }

    bagItemRepository.deleteByBagIdAndItemId(command.bagId(), command.bagItemId());
  }

  @Override
  public void addItemToBag(AddItemsToBagCommand command) {
    Integer bagId = command.bagId();
    Integer memberId = command.memberId();
    List<Integer> itemIds = command.itemIds();

    Bag bag = bagRepository.findById(bagId)
        .orElseThrow(() -> new RuntimeException("가방을 찾을 수 없습니다."));
    if (!Objects.equals(bag.getMemberId(), memberId)) {
      throw new RuntimeException("해당 가방의 소유자가 아닙니다.");
    }

    List<BagItem> existingBagItems = bagItemRepository.findAllByBagId(bagId);
    if (existingBagItems.size() + itemIds.size() >= 20) {
      throw new RuntimeException("가방에 아이템을 더 추가할 수 없습니다. 최대 20개까지 가능합니다.");
    }
    // 중복 아이템 제외하기
    List<Integer> newItemIds = itemIds.stream()
        .filter(itemId -> existingBagItems.stream()
            .noneMatch(bagItem -> bagItem.getItemId().equals(itemId)))
        .toList();

    int initialOrder = existingBagItems.size();
    List<BagItem> newBagItems = new ArrayList<>();

    for (int i = 0; i < newItemIds.size(); i++) {
      newBagItems.add(BagItem.builder()
          .bagId(bagId)
          .itemId(newItemIds.get(i))
          .itemOrder((byte) (initialOrder + i))
          .build());
    }
    bagItemRepository.saveAll(newBagItems);
    eventPublisher.publicEvent(new BagChangedEvent(memberId, bagId));
  }

  @Override
  public void removeItemsInBag(RemoveItemsInBagCommand command) {
    Bag bag = bagRepository.findById(command.bagId())
        .orElseThrow(() -> new RuntimeException("해당 가방이 존재하지 않습니다."));
    if (!Objects.equals(bag.getMemberId(), command.memberId())) {
      throw new RuntimeException("해당 가방은 사용자의 소유가 아닙니다.");
    }

    List<BagItem> existingItems = bagItemRepository.findAllByBagId(command.bagId());

    List<BagItem> itemsToDelete = existingItems.stream()
        .filter(item -> command.itemIds().contains(item.getItemId()))
        .toList();

    bagItemRepository.deleteAll(itemsToDelete);
  }
}