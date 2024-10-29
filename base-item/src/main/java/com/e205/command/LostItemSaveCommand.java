package com.e205.command;

import com.e205.commands.Command;
import java.util.List;
import org.springframework.core.io.Resource;

public record LostItemSaveCommand(
    Integer lostMemberId,
    List<Resource> images,
    Integer startRouteId,
    Integer endRouteId,
    String situationDesc,
    String itemDesc
) implements Command {

  @Override
  public String getType() {
    return "lostItemSaveCommand";
  }
}
