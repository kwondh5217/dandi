package com.e205.geo.service;

import com.e205.geo.dto.AddressResponse;
import com.e205.geo.dto.Point;
import reactor.core.publisher.Mono;

public interface GeoClient {

  String findFullAddress(Point point);

  Mono<AddressResponse> findFullAddressMono(Point point);
}
