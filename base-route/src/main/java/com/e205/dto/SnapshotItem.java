package com.e205.dto;

import lombok.Builder;

@Builder
public record SnapshotItem(
    String name,
    String emoticon,
    Integer type,
    boolean isChecked
) {

}
