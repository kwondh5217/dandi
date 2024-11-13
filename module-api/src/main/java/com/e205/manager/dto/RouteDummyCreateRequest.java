package com.e205.manager.dto;

import com.e205.dto.TrackPoint;
import java.util.List;

public record RouteDummyCreateRequest(
    String token,
    List<TrackPoint> track,
    String snapshot
) {

}
