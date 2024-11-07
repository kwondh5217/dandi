package com.e205.domain.bag.intg;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.command.bag.command.BagDeleteCommand;
import com.e205.command.bag.command.BagItemDeleteCommand;
import com.e205.command.item.command.DeleteItemCommand;
import com.e205.domain.bag.entity.Bag;
import com.e205.domain.bag.entity.BagItem;
import com.e205.domain.bag.repository.BagItemRepository;
import com.e205.domain.bag.repository.BagRepository;
import com.e205.domain.bag.service.BagCommandServiceDefault;
import com.e205.domain.item.entity.Item;
import com.e205.domain.item.repository.ItemRepository;
import com.e205.domain.item.service.ItemCommandServiceDefault;
import com.e205.events.EventPublisher;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
//@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BagCommandServiceIntegrationTest {

  @MockBean
  private EventPublisher eventPublisher;

  @Autowired
  private BagCommandServiceDefault bagCommandService;

  @Autowired
  private ItemCommandServiceDefault itemCommandServiceDefault;

  @Autowired
  private BagRepository bagRepository;

  @Autowired
  private BagItemRepository bagItemRepository;

  @Autowired
  private ItemRepository itemRepository;

  private Bag createBag(String name, Integer memberId, byte order, char enabled) {
    return bagRepository.save(
        Bag.builder().name(name).memberId(memberId).bagOrder(order).enabled(enabled).build()
    );
  }

  private BagItem createBagItem(Integer bagId, Integer itemId, byte order) {
    return bagItemRepository.save(
        BagItem.builder().bagId(bagId).itemId(itemId).itemOrder(order).build()
    );
  }

  private Item createItem(Integer memberId, String emoticon, String name, byte colorKey,
      byte order) {
    return itemRepository.save(
        Item.builder().memberId(memberId).emoticon(emoticon).name(name).colorKey(colorKey)
            .itemOrder(order).build()
    );
  }

  @DisplayName("특정 가방의 특정 아이템을 삭제한다.")
  @Test
  void deleteBagItem_ShouldDeleteSpecificBagItem() {
    // Given
    Integer memberId = 1;
    Bag targetBag1 = createBag("bag1", memberId, (byte) 1, 'Y');
    Bag targetBag2 = createBag("bag2", memberId, (byte) 2, 'N');

    BagItem bagItem1 = createBagItem(targetBag1.getId(), 1, (byte) 1);
    BagItem bagItem2 = createBagItem(targetBag1.getId(), 2, (byte) 2);
    BagItem bagItem3 = createBagItem(targetBag2.getId(), 1, (byte) 1);

    // When
    bagCommandService.deleteBagItem(
        new BagItemDeleteCommand(memberId, targetBag1.getId(), bagItem1.getId()));

    // Then
    List<BagItem> remainingItems = bagItemRepository.findAllByBagId(targetBag1.getId());
    assertThat(remainingItems).hasSize(1);
    assertThat(remainingItems)
        .extracting("itemId")
        .containsExactly(bagItem2.getItemId());

    List<BagItem> remainingItemsInTargetBag2 = bagItemRepository.findAllByBagId(targetBag2.getId());
    assertThat(remainingItemsInTargetBag2).hasSize(1);
    assertThat(remainingItemsInTargetBag2)
        .extracting("itemId")
        .containsExactly(bagItem1.getItemId());
  }

  @DisplayName("Item 삭제 시, 해당 Item과 연관된 BagItem들이 삭제된다.")
  @Test
  void deleteItem_ShouldDeleteItemAndAssociatedBagItems() {
    // Given
    Integer memberId = 1;
    Item itemToDelete = createItem(memberId, "😀", "Sample Item", (byte) 1, (byte) 1);

    Bag targetBag1 = createBag("bag1", memberId, (byte) 1, 'Y');
    Bag targetBag2 = createBag("bag2", memberId, (byte) 2, 'N');

    BagItem bagItem1 = createBagItem(targetBag1.getId(), itemToDelete.getId(), (byte) 1);
    BagItem bagItem2 = createBagItem(targetBag2.getId(), itemToDelete.getId(), (byte) 2);

    // When
    itemCommandServiceDefault.delete(new DeleteItemCommand(memberId, itemToDelete.getId()));

    // Then
    assertThat(itemRepository.findById(itemToDelete.getId())).isEmpty();
    assertThat(bagItemRepository.findAllByItemId(itemToDelete.getId())).isEmpty();
  }

  @DisplayName("가방 삭제 시, 해당 가방과 연관된 아이템들이 삭제된다.")
  @Test
  void deleteBag_ShouldDeleteBagAndAssociatedItems() {
    // Given
    Integer memberId = 1;
    Bag targetBag = createBag("bag1", memberId, (byte) 1, 'Y');

    createBagItem(targetBag.getId(), 1, (byte) 1);
    createBagItem(targetBag.getId(), 2, (byte) 2);
    createBagItem(targetBag.getId(), 3, (byte) 3);

    // When
    bagCommandService.delete(new BagDeleteCommand(memberId, targetBag.getId(), 4));

    // Then
    assertThat(bagRepository.findById(targetBag.getId())).isEmpty();
    assertThat(bagItemRepository.findAllByBagId(targetBag.getId())).isEmpty();
  }
}
