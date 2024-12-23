package com.e205.base.route.payload;

import com.e205.base.route.dto.Snapshot;
import lombok.Builder;

@Builder
public record SnapshotPayload(
    char skip,
    Snapshot snapshot
) {

}
