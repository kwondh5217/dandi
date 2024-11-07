package com.e205.domain.bag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.query.ReadAllBagItemsQuery;
import com.e205.command.bag.query.ReadAllBagsQuery;
import com.e205.command.bag.query.ReadAllItemInfoQuery;
import com.e205.command.item.payload.ItemPayload;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BagQueryServiceTest {

  @Mock
  private BagRepository bagRepository;

  @Mock
  private BagItemRepository bagItemRepository;

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private BagQueryServiceDefault bagQueryService;

  @DisplayName("회원의 모든 가방을 조회")
  @Test
  void readAllBags_ShouldReturnAllBagsForMember() {
    // Given
    Integer memberId = 1;
    Bag bag1 = Bag.builder().id(1).memberId(memberId).bagOrder((byte) 1).enabled('Y').build();
    Bag bag2 = Bag.builder().id(2).memberId(memberId).bagOrder((byte) 2).enabled('Y').build();

    given(bagRepository.findAllByMemberId(memberId)).willReturn(Arrays.asList(bag1, bag2));

    // When
    List<BagPayload> bags = bagQueryService.readAllBags(new ReadAllBagsQuery(memberId));

    // Then
    assertThat(bags).hasSize(2);
    assertThat(bags).extracting("id").containsExactly(1, 2);
    verify(bagRepository, times(1)).findAllByMemberId(memberId);
  }

  @DisplayName("특정 가방 ID에 포함된 모든 BagItem 조회")
  @Test
  void readAllBagItemsByBagId_ShouldReturnAllBagItemsForBag() {
    // Given
    Integer memberId = 1;
    Integer bagId = 1;
    BagItem bagItem1 = BagItem.builder().bagId(bagId).itemOrder((byte) 1).build();
    BagItem bagItem2 = BagItem.builder().bagId(bagId).itemOrder((byte) 2).build();

    given(bagRepository.existsByIdAndMemberId(bagId, memberId)).willReturn(true);
    given(bagItemRepository.findAllByBagId(bagId)).willReturn(Arrays.asList(bagItem1, bagItem2));

    // When
    List<BagItemPayload> bagItems = bagQueryService.readAllBagItemsByBagId(
        new ReadAllBagItemsQuery(1, 1));

    // Then
    assertThat(bagItems).hasSize(2);
    verify(bagItemRepository, times(1)).findAllByBagId(bagId);
  }

  @DisplayName("특정 Item ID 목록에 포함된 모든 Item 조회")
  @Test
  void readAllByItemIds_ShouldReturnAllItemsForGivenIds() {
    // Given
    List<Integer> itemIds = Arrays.asList(1, 2, 3);
    ReadAllItemInfoQuery query = new ReadAllItemInfoQuery(itemIds);

    Item item1 = Item.builder().id(1).name("지갑").build();
    Item item2 = Item.builder().id(2).name("여권").build();
    Item item3 = Item.builder().id(3).name("키").build();

    given(itemRepository.findAllById(itemIds)).willReturn(Arrays.asList(item1, item2, item3));

    // When
    List<ItemPayload> items = bagQueryService.readAllByItemIds(query);

    // Then
    assertThat(items).hasSize(3);
    assertThat(items).extracting("id").containsExactly(1, 2, 3);
    verify(itemRepository, times(1)).findAllById(itemIds);
  }
}