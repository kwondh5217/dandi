package com.e205.route.dto.query;

import com.e205.base.route.dto.Snapshot;
import com.e205.base.route.payload.SnapshotPayload;
import lombok.Builder;

@Builder
public record SnapshotDetailResponse(
    char skip,
    Snapshot snapshot
) {
  public static SnapshotDetailResponse fromPayload(SnapshotPayload payload) {
    return SnapshotDetailResponse.builder()
        .skip(payload.skip())
        .snapshot(payload.snapshot())
        .build();
  }
}
