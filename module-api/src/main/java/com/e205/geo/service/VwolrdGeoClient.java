package com.e205.geo.service;

import static com.e205.geo.message.GeoMessage.BLANK;
import static com.e205.geo.message.GeoMessage.COMMA;
import static com.e205.geo.message.GeoMessage.FAILED_TO_PARSE;
import static com.e205.geo.message.GeoMessage.KEY;
import static com.e205.geo.message.GeoMessage.NONE_ADDRESS;
import static com.e205.geo.message.GeoMessage.NOT_BE_NULL;
import static com.e205.geo.message.GeoMessage.POINT;
import static com.e205.geo.message.GeoMessage.RESPONSE;
import static com.e205.geo.message.GeoMessage.RESULT;
import static com.e205.geo.message.GeoMessage.STATUS;

import com.e205.exception.GlobalException;
import com.e205.geo.dto.Point;
import com.e205.item.dto.GeoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

@Component
public class VwolrdGeoClient implements GeoClient {

  private final ObjectMapper objectMapper;
  private final RestClient restClient;
  private final String apiKey;

  public VwolrdGeoClient(
      ObjectMapper objectMapper,
      @Value("${vworld.baseUrl}") String baseUrl,
      @Value("${vworld.key}") String apiKey
  ) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    this.objectMapper = objectMapper;
    this.apiKey = apiKey;
  }

  @Override
  public GeoResponse findLocation(Point point) {
    return extractResultNode(fetchLocationData(point));
  }

  @Override
  public String findFullAddress(Point point) {
    try {
      return extractResultNode(fetchLocationData(point)).text();
    } catch (RuntimeException e) {
      return NONE_ADDRESS.toString();
    }
  }

  private String fetchLocationData(Point point) {
    return this.restClient.get()
        .uri(uriBuilder -> uriBuilder
            .queryParam(POINT.toString(), point.lon() + COMMA.toString() + point.lat())
            .queryParam(KEY.toString(), apiKey)
            .build())
        .retrieve()
        .body(String.class);
  }

  private GeoResponse extractResultNode(String responseJson) {
    try {
      JsonNode rootNode = objectMapper.readTree(responseJson);
      validateResponseStatus(rootNode);

      JsonNode resultNode = rootNode.path(RESPONSE.toString()).path(RESULT.toString()).get(0);
      Assert.state(resultNode != null, NOT_BE_NULL.toString());

      return objectMapper.treeToValue(resultNode, GeoResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(FAILED_TO_PARSE.toString(), e);
    }
  }

  private void validateResponseStatus(JsonNode rootNode) {
    String status = rootNode.path(RESPONSE.toString()).path(STATUS.toString()).textValue();
    if (BLANK.equals(status)) {
      throw new GlobalException("E204");
    }
  }
}
