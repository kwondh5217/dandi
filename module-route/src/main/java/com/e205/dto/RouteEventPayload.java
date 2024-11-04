package com.e205.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

@Builder
public record RouteEventPayload(
    Integer routeId,
    char skip,
    Snapshot snapshot
) {

  private static ObjectMapper mapper = new ObjectMapper();

  public static String toJson(RouteEventPayload payload) {
    try {
      return mapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      // TODO <이현수> : 예외 구체화
      throw new RuntimeException("변환 중 예외 발생", e);
    }
  }

}
