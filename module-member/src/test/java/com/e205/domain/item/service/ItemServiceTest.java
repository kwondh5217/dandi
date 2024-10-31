package com.e205.domain.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.e205.command.item.command.CreateItemCommand;
import com.e205.command.item.command.ItemOrderCommand;
import com.e205.command.item.payload.ItemPayload;
import com.e205.command.item.command.UpdateItemCommand;
import com.e205.command.item.command.UpdateItemOrderCommand;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  private static final int MAX_ITEM_COUNT = 50;
  private static final int MAX_BAG_ITEM_COUNT = 20;

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private BagItemRepository bagItemRepository;

  @Mock
  private BagRepository bagRepository;

  @InjectMocks
  private ItemCommandServiceDefault itemCommandService;

  @InjectMocks
  private ItemQueryServiceDefault itemQueryService;

  @DisplayName("새로운 아이템 저장 성공")
  @Test
  void save_ShouldSaveNewItem() {
    // Given
    CreateItemCommand createItemCommand = new CreateItemCommand(1, "🤡", "ItemName", (byte) 1, 1);
    given(itemRepository.findAllByMemberId(createItemCommand.memberId())).willReturn(
        Collections.emptyList());
    given(bagItemRepository.findAllByBagId(createItemCommand.bagId())).willReturn(
        Collections.emptyList());

    // When
    itemCommandService.save(createItemCommand);

    // Then
    verify(itemRepository, times(1)).save(any(Item.class));
    verify(bagItemRepository, times(1)).save(any());
  }

  @DisplayName("아이템 저장 실패 - 사용자의 최대 아이템 수 초과")
  @Test
  void save_ShouldThrowException_WhenUserItemsExceedMaxCount() {
    // Given
    CreateItemCommand createItemCommand = new CreateItemCommand(1, "🤡", "ItemName", (byte) 1, 1);
    given(itemRepository.findAllByMemberId(createItemCommand.memberId())).willReturn(
        Collections.nCopies(MAX_ITEM_COUNT + 1, new Item()));

    // When // Then
    assertThatThrownBy(() -> itemCommandService.save(createItemCommand))
        .isInstanceOf(RuntimeException.class);

    verify(itemRepository, never()).save(any(Item.class));
    verify(bagItemRepository, never()).save(any());
  }

  @DisplayName("아이템 저장 실패 - 가방의 최대 아이템 수 초과")
  @Test
  void save_ShouldThrowException_WhenBagItemsExceedMaxCount() {
    // Given
    CreateItemCommand createItemCommand = new CreateItemCommand(1, "🤡", "ItemName", (byte) 1, 1);
    given(itemRepository.findAllByMemberId(createItemCommand.memberId())).willReturn(
        Collections.emptyList());
    given(bagItemRepository.findAllByBagId(createItemCommand.bagId())).willReturn(
        Collections.nCopies(MAX_BAG_ITEM_COUNT + 1, new BagItem()));

    // When // Then
    assertThatThrownBy(() -> itemCommandService.save(createItemCommand))
        .isInstanceOf(RuntimeException.class);

    verify(itemRepository, never()).save(any(Item.class));
    verify(bagItemRepository, never()).save(any());
  }

  @DisplayName("유저 아이템 조회시 유저의 아이템 반환")
  @Test
  void readAllItemsByMemberId_ShouldReturnItems() {
    // Given
    Integer memberId = 1;
    Item item1 = Item.builder().id(1).name("Item1").memberId(memberId).build();
    Item item2 = Item.builder().id(2).name("Item2").memberId(memberId).build();
    given(itemRepository.findAllByMemberId(memberId)).willReturn(Arrays.asList(item1, item2));

    // When
    List<ItemPayload> items = itemQueryService.readAllItems(memberId);

    // Then
    assertThat(items).hasSize(2);
    assertThat(items).extracting("name").containsExactly("Item1", "Item2");
  }

  @DisplayName("없는 아이템 업데이트 시 에러")
  @Test
  void update_ShouldThrowException_WhenItemNotFound() {
    // Given
    Integer itemId = 1;
    UpdateItemCommand updateCommand = new UpdateItemCommand(1, itemId, "🤡", "NewName", (byte) 1);
    given(itemRepository.findById(itemId)).willReturn(Optional.empty());

    // When // Then
    assertThatThrownBy(() -> itemCommandService.update(updateCommand))
        .isInstanceOf(RuntimeException.class);
  }

  @DisplayName("소유자가 아닌 사용자가 아이템 업데이트 시 에러")
  @Test
  void update_ShouldThrowException_WhenNotOwner() {
    // Given
    Integer itemId = 1;
    Integer currentUserId = 2;
    UpdateItemCommand updateCommand = new UpdateItemCommand(1, itemId, "🤡", "NewName", (byte) 1);

    Item item = Item.builder().id(itemId).memberId(1).name("OldName").build();
    given(itemRepository.findById(currentUserId)).willReturn(Optional.of(item));

    // When // Then
    assertThatThrownBy(() -> itemCommandService.update(updateCommand))
        .isInstanceOf(RuntimeException.class);
  }

  @DisplayName("아이템 업데이트 성공 - 중복 이름이 없는 경우")
  @Test
  void update_ShouldUpdateSuccessfully_WhenNameIsNotDuplicate() {
    // Given
    Integer itemId = 1;
    Integer memberId = 1;
    String uniqueName = "Updated Item";

    UpdateItemCommand updateCommand = new UpdateItemCommand(1, itemId, "🤡", uniqueName, (byte) 1);
    Item existingItem = Item.builder().id(itemId).memberId(memberId).name("Original Item").build();

    given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
    given(itemRepository.existsByNameAndMemberIdAndIdNot(uniqueName, memberId, itemId)).willReturn(
        false);

    // When
    itemCommandService.update(updateCommand);

    // Then
    assertThat(existingItem.getName()).isEqualTo(uniqueName);
  }

  @DisplayName("아이템 업데이트 시 중복 이름으로 인한 실패")
  @Test
  void update_ShouldThrowException_WhenNameIsDuplicate() {
    // Given
    Integer itemId = 1;
    Integer memberId = 1;
    String duplicateName = "Duplicate Item";

    UpdateItemCommand updateCommand = new UpdateItemCommand(1, itemId, "😍", duplicateName, (byte) 1);
    Item existingItem = Item.builder().id(itemId).memberId(memberId).name("Original Item").build();

    given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
    given(
        itemRepository.existsByNameAndMemberIdAndIdNot(duplicateName, memberId, itemId)).willReturn(
        true);

    // When & Then
    assertThatThrownBy(() -> itemCommandService.update(updateCommand))
        .isInstanceOf(RuntimeException.class);

    verify(itemRepository, never()).save(any(Item.class));
  }

  @DisplayName("아이템 순서 수정 시 아이템 순서 수정 성공")
  @Test
  void updateItemOrder_ShouldUpdateItem() {
    // Given
    Integer currentUserId = 1;
    Integer itemId1 = 1;
    Integer itemId2 = 2;
    Integer itemId3 = 3;

    List<ItemOrderCommand> itemOrders = Arrays.asList(
        new ItemOrderCommand(itemId1, (byte) 3),
        new ItemOrderCommand(itemId2, (byte) 1),
        new ItemOrderCommand(itemId3, (byte) 2)
    );

    UpdateItemOrderCommand updateCommand = new UpdateItemOrderCommand(currentUserId, itemOrders);

    // 스파이 객체를 사용하여 updateOrder 메서드 호출을 추적
    Item item1 = spy(
        Item.builder().id(itemId1).memberId(currentUserId).itemOrder((byte) 1).build());
    Item item2 = spy(
        Item.builder().id(itemId2).memberId(currentUserId).itemOrder((byte) 2).build());
    Item item3 = spy(
        Item.builder().id(itemId3).memberId(currentUserId).itemOrder((byte) 3).build());

    given(itemRepository.findAllByMemberId(currentUserId)).willReturn(
        Arrays.asList(item1, item2, item3));

    // When
    itemCommandService.updateItemOrder(updateCommand);

    // Then
    assertThat(item1.getItemOrder()).isEqualTo((byte) 3);
    assertThat(item2.getItemOrder()).isEqualTo((byte) 1);
    assertThat(item3.getItemOrder()).isEqualTo((byte) 2);

    verify(item1, times(1)).updateOrder((byte) 3);
    verify(item2, times(1)).updateOrder((byte) 1);
    verify(item3, times(1)).updateOrder((byte) 2);
  }
}