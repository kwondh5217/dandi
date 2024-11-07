package com.e205.item.dto;

import com.e205.command.LostItemSaveCommand;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record LostItemCreateRequest(String situationDesc, String itemDesc, List<String> images,
                                    int startRoute, int endRoute, LocalDateTime lostAt) {

  public LostItemSaveCommand toCommand(Integer memberId) {
    return LostItemSaveCommand.builder().lostMemberId(memberId).situationDesc(situationDesc)
        .itemDesc(itemDesc).images(images).startRouteId(startRoute).endRouteId(endRoute)
        .lostAt(lostAt).build();
  }
}
