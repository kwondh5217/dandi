package com.e205.communication.direct;

import com.e205.commands.Command;
import com.e205.communication.ItemCommandService;
import org.springframework.stereotype.Component;

@Component
public class DirectItemCommandService implements ItemCommandService {

  @Override
  public void dispatch(Command command) {

  }
}
