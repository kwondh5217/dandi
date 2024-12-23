package com.e205.base.member.command.member.service;

import com.e205.base.member.command.member.command.CreateManagerCommand;

public interface MemberManagerService {

  Integer createManager(CreateManagerCommand command);
}
