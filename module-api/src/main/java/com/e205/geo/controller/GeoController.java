package com.e205.geo.controller;

import com.e205.geo.dto.AddressResponse;
import com.e205.geo.dto.Point;
import com.e205.geo.service.GeoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/geo")
@RequiredArgsConstructor
@RestController
public class GeoController {

  private final GeoClient geoClient;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/address")
  public Mono<AddressResponse> readAddress(Point point) {
    return geoClient.findFullAddressMono(point);
  }
}
