package com.e205.command.member.service;

import com.e205.command.member.command.CreateManagerCommand;

public interface MemberManagerService {

  Integer createManager(CreateManagerCommand command);
}
