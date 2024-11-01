package com.e205.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;

public record Snapshot(
    Integer bagId,
    List<SnapshotItem> items
) {

  private static final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule());

  public static Snapshot fromJson(String json) {
    try {
      if (json == null) {
        return null;
      }
      return objectMapper.readValue(json, Snapshot.class);
    } catch (JsonProcessingException e) {
      // TODO <이현수> : 예외 구체화
      throw new RuntimeException("스냅샷 변환 중 예외 발생", e);
    }
  }

  public static String toJson(Snapshot snapshot) {
    try {
      return objectMapper.writeValueAsString(snapshot);
    } catch (JsonProcessingException e) {
      // TODO <이현수> : 예외 구체화
      throw new RuntimeException("스냅샷 변환 중 예외 발생", e);
    }
  }
}
