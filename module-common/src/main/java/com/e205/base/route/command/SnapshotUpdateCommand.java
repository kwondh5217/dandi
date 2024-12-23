package com.e205.base.route.command;

import com.e205.base.route.dto.Snapshot;

public record SnapshotUpdateCommand(
    Integer memberId,
    Integer routeId,
    Snapshot snapshot
) {

}
