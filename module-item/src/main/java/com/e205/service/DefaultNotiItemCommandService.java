package com.e205.service;

import com.e205.ItemCommandService;
import com.e205.NotifiedMembersCommand;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DefaultNotiItemCommandService implements ItemCommandService {

  @Override
  public void saveNotifiedMembers(List<NotifiedMembersCommand> command) {
    // TODO <fosong98> 알림에 대한 권한 저장 기능
  }
}
