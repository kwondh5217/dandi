package com.e205.geo.controller;

import com.e205.geo.dto.AddressResponse;
import com.e205.geo.dto.Point;
import com.e205.geo.service.GeoClient;
import com.e205.geo.service.VwolrdGeoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/geo")
@RequiredArgsConstructor
@RestController
public class GeoController {

  private final GeoClient geoClient;

  @GetMapping("/address")
  public ResponseEntity<AddressResponse> readAddress(@RequestBody Point point) {
    String address = geoClient.findFullAddress(point);
    return ResponseEntity.ok(new AddressResponse(address));
  }
}
