package com.e205.service;

import com.e205.ItemCommandService;
import com.e205.NotifiedMembersCommand;
import com.e205.command.LostItemGrantCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultNotiItemCommandService implements ItemCommandService {

  private static final String LOST = "LOST_ITEM";

  private final LostItemCommandService lostItemCommandService;

  @Override
  public void saveNotifiedMembers(List<NotifiedMembersCommand> command) {
    command.stream()
        .filter(noti -> noti.type().equals(LOST))
        .map(noti -> new LostItemGrantCommand(noti.memberId(), noti.resourceId()))
        .forEach(lostItemCommandService::grant);
  }
}
