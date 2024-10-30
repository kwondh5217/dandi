package com.e205.domain.bag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import com.e205.domain.bag.dto.BagDataResponse;
import com.e205.domain.bag.dto.BagItemDataResponse;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
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
    List<BagDataResponse> bags = bagQueryService.readAllBags(memberId);

    // Then
    assertThat(bags).hasSize(2);
    assertThat(bags).extracting("id").containsExactly(1, 2);
    verify(bagRepository, times(1)).findAllByMemberId(memberId);
  }

  @DisplayName("특정 가방의 모든 아이템을 조회")
  @Test
  void readAllItemsByBagId_ShouldReturnAllItemsInBag() {
    // Given
    Integer bagId = 1;
    BagItem bagItem1 = BagItem.builder().bagId(bagId).itemId(1).itemOrder((byte) 1).build();
    BagItem bagItem2 = BagItem.builder().bagId(bagId).itemId(2).itemOrder((byte) 2).build();

    BagItemDataResponse response1 = BagItemDataResponse.builder()
        .itemId(bagItem1.getItemId())
        .itemOrder(bagItem1.getItemOrder())
        .name("지갑")
        .emoticon("💼")
        .colorKey((byte) 1)
        .build();

    BagItemDataResponse response2 = BagItemDataResponse.builder()
        .itemId(bagItem2.getItemId())
        .itemOrder(bagItem2.getItemOrder())
        .name("여권")
        .emoticon("🛂")
        .colorKey((byte) 2)
        .build();

    given(bagItemRepository.findAllItemsByBagId(bagId)).willReturn(Arrays.asList(response1, response2));

    // When
    List<BagItemDataResponse> items = bagQueryService.readAllItemsByBagId(bagId);

    // Then
    assertThat(items).hasSize(2);
    assertThat(items).extracting("itemId").containsExactly(1, 2);
    verify(bagItemRepository, times(1)).findAllItemsByBagId(bagId);
  }
}