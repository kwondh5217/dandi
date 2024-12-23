package com.e205.base.item.command;

import com.e205.base.item.FoundItemType;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.geo.Point;

@Builder
public record FoundItemSaveCommand(
    Integer memberId,
    String image,
    Point location,
    FoundItemType type,
    String storageDesc,
    String itemDesc,
    String address,
    LocalDateTime foundAt
) {

}
