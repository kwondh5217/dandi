package com.e205.domain.bag.service;

import com.e205.command.bag.command.BagDeleteCommand;
import com.e205.command.bag.command.BagItemDeleteCommand;
import com.e205.command.bag.command.BagItemOrderUpdateCommand;
import com.e205.command.bag.command.BagNameUpdateCommand;
import com.e205.command.bag.command.BagOrderUpdateCommand;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.command.CopyBagCommand;
import com.e205.command.bag.command.CreateBagCommand;
import com.e205.command.bag.command.SelectBagCommand;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import jakarta.transaction.Transactional;
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
    // TODO: <홍성우> Exception 상세화
    Bag originalBag = bagRepository.findById(selectBagCommand.myBagId())
        .orElseThrow(RuntimeException::new);

    Bag targetBag = bagRepository.findById(selectBagCommand.targetBagId())
        .orElseThrow(RuntimeException::new);

    bagItemRepository.deleteAllByBagId(originalBag.getId());

    List<BagItem> newBagItems = bagItemRepository.findAllByBagId(targetBag.getId()).stream()
        .map(bagItem -> BagItem.builder()
            .bagId(originalBag.getId())
            .itemId(bagItem.getItemId())
            .itemOrder(bagItem.getItemOrder())
            .build())
        .toList();
    bagItemRepository.saveAll(newBagItems);
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
    // TODO: <홍성우> RuntimeException 상세화
    Bag targetBag = bagRepository.findById(command.bagId())
        .orElseThrow(RuntimeException::new);

    // TODO: <홍성우> RuntimeException 상세화
    if(!Objects.equals(targetBag.getId(), command.bagId())) {
      throw new RuntimeException();
    }

    bagItemRepository.deleteAllByBagId(targetBag.getId());
    bagRepository.delete(targetBag);
  }

  @Override
  public void deleteBagItem(BagItemDeleteCommand command) {

    Bag bag = bagRepository.findById(command.bagId())
        .orElseThrow(RuntimeException::new);

    if (Objects.equals(bag.getId(), command.memberMainBagId())) {
      throw new RuntimeException();
    }
    if (!Objects.equals(bag.getMemberId(), command.memberId())) {
      throw new RuntimeException();
    }

    bagItemRepository.deleteByBagIdAndItemId(command.bagId(), command.bagItemId());
  }
}