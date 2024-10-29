package com.e205.communication.direct;

import com.e205.NotifiedMembersCommand;
import com.e205.communication.ItemCommandService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DirectItemCommandService implements ItemCommandService {

  @Override
  public void saveNotifiedMembers(List<NotifiedMembersCommand> command) {



  }
}
