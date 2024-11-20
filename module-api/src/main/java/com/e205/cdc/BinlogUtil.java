package com.e205.cdc;

import com.e205.entity.FoundItem;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BinlogUtil {

  public static final String FOUND_ITEM = "FoundItem";
  public static final String LOST_ITEM = "LostItem";
  public static final String COMMENT = "Comment";
  private static final Set<String> enableTables = Set.of(FOUND_ITEM, LOST_ITEM, COMMENT);

  public static CustomFoundItemSaveEvent createFoundItemSaveEvent(FoundItem foundItem) {
    return new CustomFoundItemSaveEvent(UUID.randomUUID().toString(),
        foundItem.toPayload(), LocalDateTime.now());
  }
}
