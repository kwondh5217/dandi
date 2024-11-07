package com.e205.domain.message;

import com.e205.events.Event;
import com.e205.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("memberEventPublisher")
public class MemberEventPublisher {

  private final EventPublisher eventPublisher;

  public void publish(Event event) {
    eventPublisher.publicEvent(event);
  }
}
