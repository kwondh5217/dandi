package com.e205.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public record Snapshot(
    Integer bagId,
    List<SnapshotItem> items
) {

  private static final ObjectMapper mapper = new ObjectMapper();

  public Snapshot addItem(SnapshotItem newItem) {
    List<SnapshotItem> updatedItems = new ArrayList<>(this.items);
    updatedItems.add(newItem);
    return new Snapshot(this.bagId, updatedItems);
  }

  public Snapshot removeItem(SnapshotItem itemToRemove) {
    List<SnapshotItem> updatedItems = new ArrayList<>(this.items);
    updatedItems.removeIf(item -> item.equals(itemToRemove));
    return new Snapshot(this.bagId, updatedItems);
  }

  public static Snapshot fromJson(String json) {
    try {
      if (json == null) {
        return null;
      }
      return mapper.readValue(json, Snapshot.class);
    } catch (JsonProcessingException e) {
      // TODO <이현수> : 예외 구체화
      throw new RuntimeException("스냅샷 변환 중 예외 발생", e);
    }
  }

  public static String toJson(Snapshot snapshot) {
    try {
      return mapper.writeValueAsString(snapshot);
    } catch (JsonProcessingException e) {
      // TODO <이현수> : 예외 구체화
      throw new RuntimeException("스냅샷 변환 중 예외 발생", e);
    }
  }
}
