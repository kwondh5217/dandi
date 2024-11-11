package com.e205.item.service;

import com.e205.item.dto.GeoResponse;
import com.e205.item.dto.Point;

public interface GeoClient {
  GeoResponse findLocation(Point point);
}
