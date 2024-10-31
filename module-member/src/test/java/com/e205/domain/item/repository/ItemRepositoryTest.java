package com.e205.domain.item.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.item.entity.Item;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRepositoryTest {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private BagItemRepository bagItemRepository;

  @DisplayName("사용자의 아이템 중 최대 itemOrder를 가져오는 쿼리 동작 확인")
  @Test
  void findMaxItemOrderByMemberId_ShouldReturnMaxOrder() {
    // Given
    Integer memberId = 1;
    itemRepository.save(
        Item.builder().name("item1").emoticon("😂").memberId(memberId).itemOrder((byte) 1).build());
    itemRepository.save(
        Item.builder().name("item1").emoticon("🤔").memberId(memberId).itemOrder((byte) 3).build());
    itemRepository.save(
        Item.builder().name("item1").emoticon("👺").memberId(memberId).itemOrder((byte) 2).build());

    // When
    byte maxOrder = itemRepository.findMaxItemOrderByMemberId(memberId);

    // Then
    assertThat(maxOrder).isEqualTo((byte) 3);
  }

  @DisplayName("사용자의 모든 아이템을 가져오는 쿼리 동작 확인")
  @Test
  void findAllByMemberId_ShouldReturnAllItemsForMember() {
    // Given
    Integer memberId = 1;
    itemRepository.save(
        Item.builder().name("bag1").emoticon("😂").memberId(memberId).itemOrder((byte) 1).build());
    itemRepository.save(
        Item.builder().name("bag1").emoticon("😂").memberId(memberId).itemOrder((byte) 2).build());
    itemRepository.save(
        Item.builder().name("bag1").emoticon("😂").memberId(2).itemOrder((byte) 1).build());

    // When
    List<Item> items = itemRepository.findAllByMemberId(memberId);

    // Then
    assertThat(items).hasSize(2);
    assertThat(items).extracting("memberId").containsOnly(memberId);
  }

  @DisplayName("중복된 이름이 존재하는지 확인하는 쿼리 동작 확인 - update")
  @Test
  void existsByNameAndMemberIdAndIdNot_ShouldReturnTrue_WhenDuplicateNameExists() {
    // Given
    Integer memberId = 1;
    String itemName = "Duplicate Item";
    Item item1 = itemRepository.save(Item.builder()
        .name(itemName)
        .emoticon("😂")
        .memberId(memberId)
        .itemOrder((byte) 1)
        .build());

    itemRepository.save(Item.builder()
        .name(itemName)
        .emoticon("🤔")
        .memberId(memberId)
        .itemOrder((byte) 2)
        .build());

    // When
    boolean exists = itemRepository.existsByNameAndMemberIdAndIdNot(itemName, memberId,
        item1.getId());

    // Then
    assertThat(exists).isTrue();
  }

  @DisplayName("중복된 이름이 존재하는지 확인하는 쿼리 동작 확인 - update")
  @Test
  void existsByNameAndMemberIdAndIdNot_ShouldReturnFalse_WhenDuplicateNameExists() {
    // Given
    Integer memberId = 1;
    String itemName = "Duplicate Item";
    Item item1 = itemRepository.save(Item.builder()
        .name(itemName)
        .emoticon("😂")
        .memberId(memberId)
        .itemOrder((byte) 1)
        .build());

    itemRepository.save(Item.builder()
        .name("Unique Item")
        .emoticon("🤔")
        .memberId(memberId)
        .itemOrder((byte) 2)
        .build());

    // When
    boolean exists = itemRepository.existsByNameAndMemberIdAndIdNot(itemName, memberId,
        item1.getId());

    // Then
    assertThat(exists).isFalse();
  }

  @DisplayName("특정 가방에 포함되지 않은 아이템을 가져오는 쿼리 동작 확인")
  @Test
  void findItemsNotInBag_ShouldReturnItemsNotInSpecifiedBag() {
    // Given
    Integer memberId = 1;
    Integer bagId = 1;

    Item item1 = itemRepository.save(Item.builder()
        .name("Item 1")
        .emoticon("🤮")
        .memberId(memberId)
        .itemOrder((byte) 1)
        .build());

    Item item2 = itemRepository.save(Item.builder()
        .name("Item 2")
        .emoticon("💩")
        .memberId(memberId)
        .itemOrder((byte) 2)
        .build());

    Item item3 = itemRepository.save(Item.builder()
        .name("Item 3")
        .emoticon("🤖")
        .memberId(memberId)
        .itemOrder((byte) 3)
        .build());

    // BagItem에 포함된 아이템 설정 (item1, item2는 포함하고 item3은 포함하지 않음)
    bagItemRepository.save(
        BagItem.builder().bagId(bagId).itemId(item1.getId()).itemOrder((byte) 1).build());
    bagItemRepository.save(
        BagItem.builder().bagId(bagId).itemId(item2.getId()).itemOrder((byte) 2).build());

    // When
    List<Item> itemsNotInBag = itemRepository.findItemsNotInBag(memberId, bagId);

    // Then
    assertThat(itemsNotInBag).hasSize(1);
    assertThat(itemsNotInBag.get(0).getId()).isEqualTo(item3.getId());
  }


  @DisplayName("주어진 ID 목록에 해당하는 모든 아이템을 조회")
  @Test
  void readAllByItemIds_ShouldReturnItemsWithGivenIds() {
    // Given
    Item item1 = itemRepository.save(Item.builder()
        .name("Item A")
        .emoticon("🚗")
        .memberId(1)
        .itemOrder((byte) 1)
        .build());

    itemRepository.save(Item.builder()
        .name("Item B")
        .emoticon("🚀")
        .memberId(1)
        .itemOrder((byte) 2)
        .build());

    Item item3 = itemRepository.save(Item.builder()
        .name("Item C")
        .emoticon("🌟")
        .memberId(1)
        .itemOrder((byte) 3)
        .build());

    List<Integer> itemIds = List.of(item1.getId(), item3.getId());

    // When
    List<Item> items = itemRepository.findAllById(itemIds);

    // Then
    assertThat(items).hasSize(2);
    assertThat(items).extracting("id").containsExactlyInAnyOrder(item1.getId(), item3.getId());
  }
}