package com.e205.domain.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.e205.domain.item.dto.CreateItemCommand;
import com.e205.domain.item.dto.ItemDataResponse;
import com.e205.domain.item.dto.ItemOrder;
import com.e205.domain.item.dto.UpdateItemCommand;
import com.e205.domain.item.dto.UpdateItemOrderCommand;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import com.e205.domain.item.service.ItemCommandServiceDefault;
import com.e205.domain.item.service.ItemQueryServiceDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemCommandServiceDefault itemCommandService;

  @InjectMocks
  private ItemQueryServiceDefault itemQueryService;

  @DisplayName("새로운 아이템 저장 성공")
  @Test
  void save_ShouldSaveNewItem() {
    // Given
    CreateItemCommand createItemCommand = new CreateItemCommand(1, "🤡", "ItemName", (byte) 1, 1);

    when(itemRepository.findMaxItemOrderByMemberId(anyInt())).thenReturn((byte) 0);

    // When
    itemCommandService.save(createItemCommand);

    // Then
    verify(itemRepository, times(1)).save(any(Item.class));
  }

  @DisplayName("유저 아이템 조회시 유저의 아이템 반환")
  @Test
  void readAllItemsByMemberId_ShouldReturnItems() {
    // Given
    Integer memberId = 1;
    Item item1 = Item.builder().id(1).name("Item1").memberId(memberId).build();
    Item item2 = Item.builder().id(2).name("Item2").memberId(memberId).build();

    when(itemRepository.findAllByMemberId(memberId)).thenReturn(
        Arrays.asList(item1, item2));

    // When
    List<ItemDataResponse> items = itemQueryService.readAllItems(memberId);

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

    when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

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
    when(itemRepository.findById(currentUserId)).thenReturn(Optional.of(item));

    // When // Then
    assertThatThrownBy(() -> itemCommandService.update(updateCommand))
        .isInstanceOf(RuntimeException.class);
  }

  @DisplayName("아이템 업데이트 성공")
  @Test
  void update_ShouldUpdateExistingItem() {
    // Given
    Integer itemId = 1;
    UpdateItemCommand updateCommand = new UpdateItemCommand(1, itemId, "🤡", "NewName", (byte) 1);

    Item item = Item.builder().id(itemId).memberId(1).name("OldName").build();
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    // When
    itemCommandService.update(updateCommand);

    // Then
    assertThat(item.getName()).isEqualTo("NewName");
    assertThat(item.getEmoticon()).isEqualTo("🤡");
    assertThat(item.getColorKey()).isEqualTo((byte) 1);
  }

  @DisplayName("아이템 순서 수정 시 아이템 순서 수정 성공")
  @Test
  void updateItemOrder_ShouldUpdateItem() {
    // Given
    Integer currentUserId = 1;
    Integer itemId1 = 1;
    Integer itemId2 = 2;
    Integer itemId3 = 3;

    List<ItemOrder> itemOrders = Arrays.asList(
        new ItemOrder(itemId1, (byte) 3),
        new ItemOrder(itemId2, (byte) 1),
        new ItemOrder(itemId3, (byte) 2)
    );

    UpdateItemOrderCommand updateCommand = new UpdateItemOrderCommand(currentUserId, itemOrders);

    // 스파이 객체를 사용하여 updateOrder 메서드 호출을 추적
    Item item1 = Mockito.spy(
        Item.builder().id(itemId1).memberId(currentUserId).itemOrder((byte) 1).build());
    Item item2 = Mockito.spy(
        Item.builder().id(itemId2).memberId(currentUserId).itemOrder((byte) 2).build());
    Item item3 = Mockito.spy(
        Item.builder().id(itemId3).memberId(currentUserId).itemOrder((byte) 3).build());

    when(itemRepository.findAllByMemberId(currentUserId)).thenReturn(
        Arrays.asList(item1, item2, item3));

    // When
    itemCommandService.updateItemOrder(updateCommand);

    // Then
    assertThat(item1.getItemOrder()).isEqualTo((byte) 3);
    assertThat(item2.getItemOrder()).isEqualTo((byte) 1);
    assertThat(item3.getItemOrder()).isEqualTo((byte) 2);

    // 각 아이템의 updateOrder가 올바르게 호출되었는지 검증
    verify(item1, times(1)).updateOrder((byte) 3);
    verify(item2, times(1)).updateOrder((byte) 1);
    verify(item3, times(1)).updateOrder((byte) 2);
  }
}
