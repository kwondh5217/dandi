package com.e205.cdc;

import com.e205.FoundItemType;
import com.e205.event.FoundItemSaveEvent;
import com.e205.payload.FoundItemPayload;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

public class BinlogMappingUtils {

  public static FoundItemSaveEvent mapToFoundItemPayload(Map<String, Object> source) {
    FoundItemPayload foundItemPayload = new FoundItemPayload(
        (Integer) source.get("id"),
        (Integer) source.get("memberId"),
        (Double) source.get("lat"),
        (Double) source.get("lon"),
        (String) source.get("description"),
        (String) source.get("savePlace"),
        resolveFoundItemType(source.get("type")),
        (String) source.get("address"),
        resolveLocalDateTime(source.get("foundAt"))
    );
    return new FoundItemSaveEvent(foundItemPayload, LocalDateTime.now());
  }

  private static FoundItemType resolveFoundItemType(Object type) {
    if (type instanceof Integer) {
      return FoundItemType.values()[((Integer) type) - 1];
    } else if (type instanceof String) {
      return FoundItemType.valueOf((String) type);
    }
    throw new IllegalArgumentException("Unsupported type for FoundItemType: " + type);
  }

  private static LocalDateTime resolveLocalDateTime(Object date) {
    if (date instanceof LocalDateTime) {
      return (LocalDateTime) date;
    } else if (date instanceof Date) {
      return ((Date) date).toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime();
    }
    throw new IllegalArgumentException("Unsupported type for date: " + date);
  }
}
