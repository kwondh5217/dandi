package com.e205.item.service;

import com.e205.exception.GlobalException;
import com.e205.item.dto.GeoResponse;
import com.e205.item.dto.Point;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

@Component
public class VwolrdGeoClient implements GeoClient {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final String apiKey;

  public VwolrdGeoClient(
      ObjectMapper objectMapper,
      @Value("${vworld.baseUrl}") String baseUrl,
      @Value("${vworld.key}") String apiKey) {
    this.restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .build();
    this.objectMapper = objectMapper;
    this.apiKey = apiKey;
  }

  @Override
  public GeoResponse findLocation(Point point) {
    return extractResultNode(fetchLocationData(point));
  }

  private String fetchLocationData(Point point) {
    return this.restClient.get()
        .uri(uriBuilder -> uriBuilder
            .queryParam("point", point.lat() + "," + point.lon())
            .queryParam("key", apiKey)
            .build())
        .retrieve()
        .body(String.class);
  }

  private GeoResponse extractResultNode(String responseJson) {
    try {
      JsonNode rootNode = objectMapper.readTree(responseJson);
      validateResponseStatus(rootNode);

      JsonNode resultNode = rootNode.path("response").path("result").get(0);
      Assert.state(resultNode != null, "resultNode must not be null");

      return objectMapper.treeToValue(resultNode, GeoResponse.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse location data", e);
    }
  }

  private void validateResponseStatus(JsonNode rootNode) {
    String status = rootNode.path("response").path("status").textValue();
    if ("NOT FOUND".equals(status)) {
      throw new GlobalException("E204");
    }
  }
}
