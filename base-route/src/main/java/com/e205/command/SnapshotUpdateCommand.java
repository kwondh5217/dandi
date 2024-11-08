package com.e205.command;

import com.e205.dto.Snapshot;

public record SnapshotUpdateCommand(
    Integer memberId,
    Integer routeId,
    Snapshot snapshot
) {

}
