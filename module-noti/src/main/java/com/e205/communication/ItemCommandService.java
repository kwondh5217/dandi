package com.e205.communication;

import com.e205.NotifiedMembersCommand;
import java.util.List;

public interface ItemCommandService {

  void saveNotifiedMembers(List<NotifiedMembersCommand> command);

}
