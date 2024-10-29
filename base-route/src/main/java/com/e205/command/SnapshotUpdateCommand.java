package com.e205.command;

public record SnapshotUpdateCommand(
    Integer routeId,
    String snapshot
) {

}
