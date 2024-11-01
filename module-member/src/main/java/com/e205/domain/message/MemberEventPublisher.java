package com.e205.domain.message;

import com.e205.events.Event;
import com.e205.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("memberEventPublisher")
public class MemberEventPublisher implements EventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void publish(Event event) {
    eventPublisher.publishEvent(event);
  }
}
