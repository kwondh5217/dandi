package com.e205.geo.service;

import com.e205.item.dto.GeoResponse;
import com.e205.geo.dto.Point;

public interface GeoClient {
  GeoResponse findLocation(Point point);
  String findFullAddress(Point point);
}
