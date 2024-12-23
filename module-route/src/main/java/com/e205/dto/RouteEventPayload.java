package com.e205.dto;

import com.e205.base.route.dto.Snapshot;
import com.e205.exception.GlobalException;
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
      throw new GlobalException("E004");
    }
  }

}
