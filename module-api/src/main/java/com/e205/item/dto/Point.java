package com.e205.item.dto;

public record Point(
    Double lat,
    Double lon
) {

  public org.springframework.data.geo.Point toGeoPoint() {
    return new org.springframework.data.geo.Point(lat, lon);
  }
}
