package com.e205.command;

import com.e205.FoundItemType;
import java.time.LocalDateTime;
import org.springframework.core.io.Resource;
import org.springframework.data.geo.Point;

public record FoundItemSaveCommand(
    Integer memberId,
    Resource image,
    Point location,
    FoundItemType type,
    String storageDesc,
    String itemDesc,
    LocalDateTime foundAt
) {

}
