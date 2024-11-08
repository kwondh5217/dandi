package com.e205.event;

import com.e205.byteBuddy.PersistEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestEventService {

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Transactional
  @PersistEvent
  public void save(TestEvent event) {
    eventPublisher.publishEvent(event);
  }

  @Transactional
  public void saveNotOutbox(TestEvent event) {
    eventPublisher.publishEvent(event);
  }
}
