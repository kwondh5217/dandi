package com.e205.cdc;

import static org.junit.jupiter.api.Assertions.*;

import com.e205.FoundItemType;
import com.e205.entity.FoundItem;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BinlogUtilTest {

  @Test
  void createFoundItemEvent() {
    FoundItem foundItem = FoundItem.builder()
        .memberId(1)
        .lat(37.7749)
        .lon(122.4194)
        .description("Found item description")
        .savePlace("Lost and Found Center")
        .type(FoundItemType.OTHER)
        .foundAt(LocalDateTime.of(2024, 11, 19, 12, 0))
        .address("123 Main Street, San Francisco, CA")
        .build();

    CustomFoundItemSaveEvent foundItemSaveEvent = BinlogUtil.createFoundItemSaveEvent(
        foundItem);

    assertNotNull(foundItemSaveEvent);
    assertNotNull(foundItemSaveEvent.eventId());
  }

}