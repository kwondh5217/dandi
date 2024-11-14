package com.e205.route.dto.command;

import com.e205.dto.Snapshot;
import jakarta.validation.constraints.NotNull;

public record SnapshotUpdateRequest(
    @NotNull(message = "스냅샷은 null 이 될 수 없습니다.")
    Snapshot snapshot
) {

}
