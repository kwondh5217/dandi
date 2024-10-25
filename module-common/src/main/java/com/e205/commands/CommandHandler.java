package com.e205.commands;

public interface CommandHandler<T extends Command> {

  void handle(Command command);
}
