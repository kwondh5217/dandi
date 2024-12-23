package com.e205.base.route.command;

import com.e205.base.route.dto.TrackPoint;
import java.util.List;
import lombok.Builder;

@Builder
public record RouteDummyCreateCommand(
    Integer memberId,
    List<TrackPoint> track,
    String snapshot,
    String startAddress,
    String endAddress
) {

  public static RouteDummyCreateCommand toCommand(
      Integer memberId,
      List<TrackPoint> track,
      String snapshot,
      String startAddress,
      String endAddress
  ) {
    return RouteDummyCreateCommand.builder()
        .memberId(memberId)
        .track(track)
        .snapshot(snapshot)
        .startAddress(startAddress)
        .endAddress(endAddress)
        .build();
  }
}
