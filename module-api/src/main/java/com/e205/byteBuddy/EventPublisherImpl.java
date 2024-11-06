package com.e205.byteBuddy;

import com.e205.events.Event;
import com.e205.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EventPublisherImpl implements EventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  @PersistEvent
  @Override
  public void publishAtLeastOnce(Event event) {
    eventPublisher.publishEvent(event);
  }

  @Override
  public void publicEvent(Event event) {
    eventPublisher.publishEvent(event);
  }
}
