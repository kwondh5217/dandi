package com.e205.events;

public interface EventListener<T extends Event> {

  void handle(Event event);
}
