package com.e205.command;

import com.e205.FoundItemType;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.data.geo.Point;

@Builder
public record FoundItemSaveCommand(
    Integer memberId,
    String image,
    Point location,
    FoundItemType type,
    String storageDesc,
    String itemDesc,
    LocalDateTime foundAt
) {

}
