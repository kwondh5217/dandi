package com.e205.service;

import com.e205.base.item.service.LostItemCommandService;
import com.e205.base.noti.ItemCommandService;
import com.e205.base.noti.NotifiedMembersCommand;
import com.e205.base.item.command.LostItemGrantCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultNotiItemCommandService implements ItemCommandService {

  private static final String LOST = "lostItem";

  private final LostItemCommandService lostItemCommandService;

  @Override
  public void saveNotifiedMembers(List<NotifiedMembersCommand> command) {
    command.stream()
        .filter(noti -> noti.type().equals(LOST))
        .map(noti -> new LostItemGrantCommand(noti.memberId(), noti.resourceId()))
        .forEach(lostItemCommandService::grant);
  }
}
