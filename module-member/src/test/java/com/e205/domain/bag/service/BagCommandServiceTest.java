package com.e205.domain.bag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.e205.base.member.command.bag.command.BagItemOrderCommand;
import com.e205.base.member.command.bag.command.BagItemOrderUpdateCommand;
import com.e205.base.member.command.bag.command.BagNameUpdateCommand;
import com.e205.base.member.command.bag.command.BagOrderCommand;
import com.e205.base.member.command.bag.command.BagOrderUpdateCommand;
import com.e205.base.member.command.bag.command.CopyBagCommand;
import com.e205.base.member.command.bag.command.CreateBagCommand;
import com.e205.base.member.command.bag.command.SelectBagCommand;
import com.e205.base.member.command.bag.event.BagChangedEvent;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.item.repository.ItemRepository;
import com.e205.events.EventPublisher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BagCommandServiceTest {

  private static final int MAX_BAG_COUNT = 10;

  @Mock
  private BagRepository bagRepository;

  @Mock
  private BagItemRepository bagItemRepository;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private EventPublisher eventPublisher;

  @InjectMocks
  private BagCommandServiceDefault bagCommandService;

  @DisplayName("새로운 가방 생성 성공")
  @Test
  void createBag_ShouldSave() {
    // Given
    Integer memberId = 1;
    CreateBagCommand createBagCommand = new CreateBagCommand(memberId, "Something");

    // When
    bagCommandService.save(createBagCommand);

    // Then
    verify(bagRepository, times(1)).save(any(Bag.class));
  }

  @DisplayName("특정 갯수 이상 생성 시 에러")
  @Test
  void save_ShouldThrowErrorWhenTooMuch() {
    // Given
    Integer memberId = 1;
    CreateBagCommand createBagCommand = new CreateBagCommand(memberId, "New Bag");

    List<Bag> existingBags = new ArrayList<>(Collections.nCopies(MAX_BAG_COUNT, new Bag()));
    given(bagRepository.findAllByMemberId(memberId)).willReturn(existingBags);

    // When & Then
    assertThatThrownBy(() -> bagCommandService.save(createBagCommand))
        .isInstanceOf(RuntimeException.class);

    verify(bagRepository, never()).save(any(Bag.class));
  }

  @DisplayName("가방 순서 업데이트 성공")
  @Test
  void updateBagOrder_ShouldUpdateBagOrder() {
    // Given
    Integer memberId = 1;
    Bag bag1 = Bag.builder().id(1).memberId(memberId).bagOrder((byte) 1).build();
    Bag bag2 = Bag.builder().id(2).memberId(memberId).bagOrder((byte) 2).build();
    Bag bag3 = Bag.builder().id(3).memberId(memberId).bagOrder((byte) 3).build();

    List<BagOrderCommand> bagOrders = Arrays.asList(
        new BagOrderCommand(1, (byte) 3),
        new BagOrderCommand(2, (byte) 1),
        new BagOrderCommand(3, (byte) 2)
    );

    BagOrderUpdateCommand bagOrderUpdateCommand = new BagOrderUpdateCommand(memberId, bagOrders);

    given(bagRepository.findAllByMemberId(memberId)).willReturn(Arrays.asList(bag1, bag2, bag3));

    // When
    bagCommandService.updateBagOrder(bagOrderUpdateCommand);

    // Then
    assertThat(bag1.getBagOrder()).isEqualTo((byte) 3);
    assertThat(bag2.getBagOrder()).isEqualTo((byte) 1);
    assertThat(bag3.getBagOrder()).isEqualTo((byte) 2);
  }

  @DisplayName("가방 이름 업데이트 성공")
  @Test
  void updateBagName_ShouldUpdateBagName() {
    Integer memberId = 1;
    Integer bagId = 123;
    Bag existingBag = Bag.builder().id(bagId).memberId(memberId).name("Old Name").build();

    BagNameUpdateCommand command = new BagNameUpdateCommand(memberId, bagId, "New Name");

    given(bagRepository.findByIdAndMemberId(bagId, memberId)).willReturn(Optional.of(existingBag));
    given(bagRepository.existsByMemberIdAndName(memberId, "New Name")).willReturn(false);

    bagCommandService.updateBagName(command);

    assertThat(existingBag.getName()).isEqualTo("New Name");
  }

  @DisplayName("가방 이름 업데이트 실패 - 중복된 이름이 존재하는 경우")
  @Test
  void updateBagName_ShouldThrowException_WhenNameIsDuplicate() {
    // Given
    Integer memberId = 1;
    Integer bagId = 1;
    String duplicateName = "중복 가방 이름";

    BagNameUpdateCommand bagNameUpdateCommand = new BagNameUpdateCommand(memberId, bagId,
        duplicateName);

    Bag bag = Bag.builder().id(bagId).memberId(memberId).name("기존 이름").build();
    given(bagRepository.findByIdAndMemberId(bagId, memberId))
        .willReturn(Optional.of(bag));

    given(bagRepository.existsByMemberIdAndName(memberId, duplicateName)).willReturn(true);

    // When & Then
    assertThatThrownBy(() -> bagCommandService.updateBagName(bagNameUpdateCommand))
        .isInstanceOf(RuntimeException.class);
    verify(bagRepository, never()).save(any(Bag.class));
  }

  @DisplayName("가방 이름 업데이트 실패 - 소유자가 아닌 경우")
  @Test
  void updateBagName_ShouldThrowException_WhenNotOwner() {
    // Given
    Integer anotherMemberId = 2;
    Integer bagId = 1;
    String newName = "새로운 가방 이름";

    BagNameUpdateCommand bagNameUpdateCommand = new BagNameUpdateCommand(anotherMemberId, bagId,
        newName);

    given(bagRepository.findByIdAndMemberId(bagId, anotherMemberId))
        .willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bagCommandService.updateBagName(bagNameUpdateCommand))
        .isInstanceOf(RuntimeException.class);

    verify(bagRepository, never()).save(any(Bag.class));
  }

  @DisplayName("가방 소지품 순서 업데이트 성공 - 소유자 검증 포함")
  @Test
  void updateBagItemOrder_ShouldUpdateItemOrder_WithOwnerVerification() {
    // Given
    Integer memberId = 1;
    Integer bagId = 1;

    List<BagItemOrderCommand> itemOrders = List.of(
        new BagItemOrderCommand(1, (byte) 3),
        new BagItemOrderCommand(2, (byte) 1),
        new BagItemOrderCommand(3, (byte) 2)
    );

    BagItemOrderUpdateCommand command = new BagItemOrderUpdateCommand(memberId, bagId, itemOrders);

    // Bag 및 BagItem mock 생성
    Bag bag = Bag.builder().id(bagId).memberId(memberId).build();
    BagItem item1 = spy(BagItem.builder().itemId(1).bagId(bagId).itemOrder((byte) 1).build());
    BagItem item2 = spy(BagItem.builder().itemId(2).bagId(bagId).itemOrder((byte) 2).build());
    BagItem item3 = spy(BagItem.builder().itemId(3).bagId(bagId).itemOrder((byte) 3).build());

    // bag과 bagItems에 대한 리포지토리 동작 정의
    given(bagRepository.findById(bagId)).willReturn(Optional.of(bag));
    given(bagItemRepository.findAllByBagId(bagId)).willReturn(List.of(item1, item2, item3));

    // When
    bagCommandService.updateBagItemOrder(command);

    // Then
    assertThat(item1.getItemOrder()).isEqualTo((byte) 3);
    assertThat(item2.getItemOrder()).isEqualTo((byte) 1);
    assertThat(item3.getItemOrder()).isEqualTo((byte) 2);

    verify(item1, times(1)).updateOrder((byte) 3);
    verify(item2, times(1)).updateOrder((byte) 1);
    verify(item3, times(1)).updateOrder((byte) 2);
  }

  @DisplayName("가방 소지품 순서 업데이트 실패 - 잘못된 소유자")
  @Test
  void updateBagItemOrder_ShouldThrowException_WhenNotOwner() {
    // Given
    Integer memberId = 1;
    Integer wrongMemberId = 2;
    Integer bagId = 1;

    List<BagItemOrderCommand> itemOrders = List.of(
        new BagItemOrderCommand(1, (byte) 3),
        new BagItemOrderCommand(2, (byte) 1)
    );

    BagItemOrderUpdateCommand command = new BagItemOrderUpdateCommand(wrongMemberId, bagId,
        itemOrders);

    Bag bag = Bag.builder().id(bagId).memberId(memberId).build();

    given(bagRepository.findById(bagId)).willReturn(Optional.of(bag));

    // When & Then
    assertThatThrownBy(() -> bagCommandService.updateBagItemOrder(command))
        .isInstanceOf(RuntimeException.class);

    verify(bagItemRepository, never()).saveAll(anyList());
  }

  @DisplayName("가방 선택 성공 - 기존 아이템 교체")
  @Test
  void selectBag_ShouldReplaceItemsInOriginalBag() {
    // Given
    Integer myBagId = 1;
    Integer targetBagId = 2;
    Integer memberId = 1;

    SelectBagCommand command = new SelectBagCommand(myBagId, targetBagId, memberId);

    // 원본 및 타겟 가방 생성
    Bag originalBag = Bag.builder().id(myBagId).memberId(memberId).build();
    Bag targetBag = Bag.builder().id(targetBagId).memberId(memberId).build();

    // 타겟 가방의 아이템 생성
    BagItem item1 = BagItem.builder().bagId(targetBagId).itemId(1).itemOrder((byte) 1).build();
    BagItem item2 = BagItem.builder().bagId(targetBagId).itemId(2).itemOrder((byte) 2).build();
    // Item itemEntity1 = Item.builder().id(1).memberId(memberId).emoticon("🙂").name("Item1").colorKey((byte)1).itemOrder((byte)1).build();
    // Item itemEntity2 = Item.builder().id(2).memberId(memberId).emoticon("😊").name("Item2").colorKey((byte)2).itemOrder((byte)2).build();

    given(bagRepository.findById(myBagId)).willReturn(Optional.of(originalBag));
    given(bagRepository.findById(targetBagId)).willReturn(Optional.of(targetBag));
    given(bagItemRepository.findAllByBagId(targetBagId)).willReturn(List.of(item1, item2));
    // given(itemRepository.findAllById(anyList())).willReturn(List.of(itemEntity1, itemEntity2));

    // When
    bagCommandService.selectBag(command);

    // Then
    verify(bagItemRepository, times(1)).deleteAllByBagId(myBagId);
    verify(bagItemRepository, times(1)).saveAll(argThat(savedItems -> {
      List<BagItem> itemList = StreamSupport.stream(savedItems.spliterator(), false).toList();
      return itemList.size() == 2 &&
          itemList.stream()
              .anyMatch(bagItem -> bagItem.getItemId() == 1 && bagItem.getItemOrder() == (byte) 1)
          &&
          itemList.stream()
              .anyMatch(bagItem -> bagItem.getItemId() == 2 && bagItem.getItemOrder() == (byte) 2);
    }));

    verify(eventPublisher, times(1)).publicEvent(any(BagChangedEvent.class));
  }

  @DisplayName("가방 복사 실패 - 최대 가방 개수 초과")
  @Test
  void copyBag_ShouldThrowException_WhenExceedsMaxBagCount() {
    // Given
    Integer memberId = 1;
    Integer bagsId = 1;
    CopyBagCommand command = new CopyBagCommand(memberId, bagsId, "새로운 가방 이름");

    Bag originalBag = Bag.builder().id(bagsId).memberId(memberId).build();

    given(bagRepository.findById(bagsId)).willReturn(Optional.of(originalBag));
    given(bagRepository.findAllByMemberId(memberId)).willReturn(
        List.of(new Bag(), new Bag(), new Bag(), new Bag(), new Bag(), new Bag(), new Bag(),
            new Bag(), new Bag(), new Bag())
    );

    // When // Then
    assertThatThrownBy(() -> bagCommandService.copyBag(command))
        .isInstanceOf(RuntimeException.class);

    verify(bagRepository, never()).save(any(Bag.class));
    verify(bagItemRepository, never()).saveAll(anyList());
  }

  @DisplayName("가방 복사 성공")
  @Test
  void copyBag_ShouldCopyBagSuccessfully() {
    // Given
    Integer memberId = 1;
    Integer bagsId = 1;
    CopyBagCommand command = new CopyBagCommand(memberId, bagsId, "새로운 가방 이름");

    Bag originalBag = Bag.builder().id(bagsId).memberId(memberId).build();

    given(bagRepository.findById(bagsId)).willReturn(Optional.of(originalBag));
    given(bagRepository.findAllByMemberId(memberId)).willReturn(
        List.of(new Bag(), new Bag())
    );
    bagCommandService.copyBag(command);

    // When // Then
    verify(bagRepository, times(1)).save(any(Bag.class));
    verify(bagItemRepository, times(1)).saveAll(anyList());
  }
}