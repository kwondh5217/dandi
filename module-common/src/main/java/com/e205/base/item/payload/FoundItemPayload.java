package com.e205.base.item.payload;

import com.e205.base.item.FoundItemType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FoundItemPayload(
    Integer id,
    Integer memberId,
    Double lat,
    Double lon,
    String description,
    String savePlace,
    FoundItemType type,
    String address,
    LocalDateTime foundAt
) {

}
