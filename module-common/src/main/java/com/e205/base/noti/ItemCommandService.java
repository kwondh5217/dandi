package com.e205.base.noti;

import java.util.List;

public interface ItemCommandService {

  void saveNotifiedMembers(List<NotifiedMembersCommand> command);

}
