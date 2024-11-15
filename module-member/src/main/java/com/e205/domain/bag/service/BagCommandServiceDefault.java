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
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.exception.MemberError;
import com.e205.domain.item.repository.ItemRepository;
import com.e205.domain.member.entity.Member;
import com.e205.domain.member.repository.MemberRepository;
import com.e205.events.EventPublisher;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
  private final MemberRepository memberRepository;

  @Override
  public void save(CreateBagCommand createBagCommand) {
    Integer memberId = createBagCommand.memberId();

    List<Bag> userBags = bagRepository.findAllByMemberId(memberId);
    if (userBags.size() >= MAX_BAG_COUNT) {
      throw MemberError.MAX_BAG_COUNT_EXCEEDED.getGlobalException();
    }

    if (bagRepository.existsByMemberIdAndName(memberId, createBagCommand.name())) {
      throw MemberError.BAG_NAME_ALREADY_EXISTS.getGlobalException();
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
        .createdAt(LocalDateTime.now())
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
    Bag bag = bagRepository.findByIdAndMemberId(
            bagNameUpdateCommand.bagId(),
            bagNameUpdateCommand.memberId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    if (bagRepository.existsByMemberIdAndName(bag.getMemberId(), bagNameUpdateCommand.name())) {
      throw MemberError.BAG_NAME_ALREADY_EXISTS.getGlobalException();
    }
    bag.updateName(bagNameUpdateCommand.name());
  }

  @Override
  public void selectBag(SelectBagCommand selectBagCommand) {
    Integer memberId = selectBagCommand.memberId();
    Bag originalBag = bagRepository.findById(selectBagCommand.myBagId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    Bag targetBag = bagRepository.findById(selectBagCommand.targetBagId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    bagItemRepository.deleteAllByBagId(originalBag.getId());

    Integer targetBagId = targetBag.getId();
    List<BagItem> newBagItems = bagItemRepository.findAllByBagId(targetBagId).stream()
        .map(bagItem -> BagItem.builder()
            .bagId(originalBag.getId())
            .itemId(bagItem.getItemId())
            .createdAt(LocalDateTime.now())
            .itemOrder(bagItem.getItemOrder())
            .build())
        .toList();

    bagItemRepository.saveAll(newBagItems);
    eventPublisher.publishAtLeastOnce(new BagChangedEvent(memberId, targetBagId));
  }

  @Override
  public void updateBagItemOrder(BagItemOrderUpdateCommand bagItemOrderUpdateCommand) {

    Bag bag = bagRepository.findById(bagItemOrderUpdateCommand.bagId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    if (!Objects.equals(bag.getMemberId(), bagItemOrderUpdateCommand.memberId())) {
      throw MemberError.BAG_NOT_OWNED_BY_USER.getGlobalException();
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
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    Integer memberId = copyBagCommand.memberId();
    if (bagRepository.findAllByMemberId(memberId).size() >= MAX_BAG_COUNT) {
      throw MemberError.MAX_BAG_COUNT_EXCEEDED.getGlobalException();
    }

    Bag newBag = Bag.builder()
        .memberId(memberId)
        .name(copyBagCommand.newBagName())
        .bagOrder((byte) (bagRepository.findMaxBagOrderByMemberId(memberId) + 1))
        .enabled('Y')
        .createdAt(LocalDateTime.now())
        .build();
    bagRepository.save(newBag);

    List<BagItem> newBagItems = bagItemRepository.findAllByBagId(copyBag.getId()).stream()
        .map(bagItem -> BagItem.builder()
            .bagId(newBag.getId())
            .itemId(bagItem.getItemId())
            .itemOrder(bagItem.getItemOrder())
            .createdAt(LocalDateTime.now())
            .build())
        .toList();
    bagItemRepository.saveAll(newBagItems);

    return newBag.toPayload();
  }

  @Override
  public void delete(BagDeleteCommand command) {
    if (Objects.equals(command.memberBagId(), command.bagId())) {
      throw MemberError.CANNOT_DELETE_DEFAULT_BAG.getGlobalException();
    }

    Bag targetBag = bagRepository.findById(command.bagId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    if (!Objects.equals(targetBag.getMemberId(), command.memberId())) {
      throw MemberError.BAG_NOT_OWNED_BY_USER.getGlobalException();
    }

    bagItemRepository.deleteAllByBagId(targetBag.getId());
    bagRepository.delete(targetBag);
  }

  @Override
  public void deleteBagItem(BagItemDeleteCommand command) {

    Bag bag = bagRepository.findById(command.bagId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);

    if (!Objects.equals(bag.getMemberId(), command.memberId())) {
      throw MemberError.BAG_NOT_OWNED_BY_USER.getGlobalException();
    }

    bagItemRepository.deleteByBagIdAndItemId(command.bagId(), command.bagItemId());
  }

  @Override
  public void addItemToBag(AddItemsToBagCommand command) {
    Integer bagId = command.bagId();
    Integer memberId = command.memberId();
    Member member = memberRepository.findById(memberId)
        .orElseThrow(MemberError.USER_NOT_FOUND::getGlobalException);

    List<Integer> itemIds = command.itemIds();

    Bag bag = bagRepository.findById(bagId)
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);
    if (!Objects.equals(bag.getMemberId(), memberId)) {
      throw MemberError.BAG_NOT_OWNED_BY_USER.getGlobalException();
    }

    List<BagItem> existingBagItems = bagItemRepository.findAllByBagId(bagId);
    if (existingBagItems.size() + itemIds.size() >= 20) {
      throw MemberError.MAX_BAG_ITEM_COUNT_EXCEEDED.getGlobalException();
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
          .createdAt(LocalDateTime.now())
          .itemOrder((byte) (initialOrder + i))
          .build());
    }
    bagItemRepository.saveAll(newBagItems);
    if (member.getBagId().equals(bagId)) {
      eventPublisher.publishAtLeastOnce(new BagChangedEvent(memberId, bagId));
    }
  }

  @Override
  public void removeItemsInBag(RemoveItemsInBagCommand command) {
    Bag bag = bagRepository.findById(command.bagId())
        .orElseThrow(MemberError.BAG_NOT_FOUND::getGlobalException);
    if (!Objects.equals(bag.getMemberId(), command.memberId())) {
      throw MemberError.BAG_NOT_OWNED_BY_USER.getGlobalException();
    }

    List<BagItem> existingItems = bagItemRepository.findAllByBagId(command.bagId());

    List<BagItem> itemsToDelete = existingItems.stream()
        .filter(item -> command.itemIds().contains(item.getItemId()))
        .toList();

    bagItemRepository.deleteAll(itemsToDelete);
  }
}