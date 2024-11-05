package com.e205.message;

import com.e205.events.Event;
import com.e205.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ItemEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public void publish(Event event) {
    eventPublisher.publishEvent(event);
  }
}

