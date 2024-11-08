package com.e205.command;

import com.e205.dto.TrackPoint;
import java.util.List;
import lombok.Builder;

@Builder
public record RouteDummyCreateCommand(
    Integer memberId,
    List<TrackPoint> track,
    String snapshot
) {

  public static RouteDummyCreateCommand toCommand(
      Integer memberId,
      List<TrackPoint> track,
      String snapshot
  ) {
    return RouteDummyCreateCommand.builder()
        .memberId(memberId)
        .track(track)
        .snapshot(snapshot)
        .build();
  }
}
