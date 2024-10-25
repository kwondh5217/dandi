package com.e205.communication.commands;

import com.e205.commands.Command;
import com.e205.commands.CommandDispatcher;
import com.e205.commands.CommandHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class NotificationDispatcher implements CommandDispatcher {

  private Map<String, CommandHandler<? extends Command>> handlers = new HashMap<>();

  public <T extends Command> void registerHandler(String commandType,
      CommandHandler<T> handler) {
    handlers.put(commandType, handler);
  }

  @Override
  public void dispatch(Command command) {
    Optional.ofNullable(handlers.get(command.getType())).ifPresentOrElse(
        handler -> handler.handle(command),
        () -> {
          throw new RuntimeException("cannot handle command of type " + command.getType());
        }
    );
  }
}
