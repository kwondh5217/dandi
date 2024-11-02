package com.e205.payload;

import com.e205.dto.Snapshot;
import lombok.Builder;

@Builder
public record SnapshotPayload(
    char skip,
    Snapshot snapshot
) {

}
