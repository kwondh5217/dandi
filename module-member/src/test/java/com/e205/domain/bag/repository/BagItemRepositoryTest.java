package com.e205.domain.bag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.item.repository.ItemRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BagItemRepositoryTest {

  @Autowired
  BagItemRepository bagItemRepository;

  @Autowired
  ItemRepository itemRepository;

  @DisplayName("특정 가방 ID에 포함된 모든 BagItem 조회")
  @Test
  void findAllByBagId_ShouldReturnAllBagItems() {
    // Given
    Integer bagId = 1;

    BagItem bagItem1 = BagItem.builder().bagId(bagId).itemId(1).itemOrder((byte) 1).build();
    BagItem bagItem2 = BagItem.builder().bagId(bagId).itemId(2).itemOrder((byte) 2).build();

    bagItemRepository.save(bagItem1);
    bagItemRepository.save(bagItem2);

    // When
    List<BagItem> bagItems = bagItemRepository.findAllByBagId(bagId);

    // Then
    assertThat(bagItems).hasSize(2);
    assertThat(bagItems).extracting("itemId").containsExactly(1, 2);
  }
}