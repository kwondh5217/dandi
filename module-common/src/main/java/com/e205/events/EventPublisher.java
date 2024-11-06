package com.e205.events;

public interface EventPublisher {

  void publishAtLeastOnce(Event event);

  void publicEvent(Event event);

}
