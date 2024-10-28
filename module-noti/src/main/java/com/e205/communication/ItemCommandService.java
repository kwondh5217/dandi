package com.e205.communication;

import com.e205.commands.Command;

public interface ItemCommandService {

  void dispatch(Command command);

}
