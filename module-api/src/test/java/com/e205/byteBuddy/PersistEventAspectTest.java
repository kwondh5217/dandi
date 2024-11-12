package com.e205.byteBuddy;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.event.TestEvent;
import com.e205.events.EventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class PersistEventAspectTest {

  @Autowired
  private EventPublisher eventPublisher;
  @SpyBean
  private PersistEventAspect persistEventAspect;
  @SpyBean
  private EventConverter eventConverter;

  @Transactional
  @Rollback(value = false)
  @Test
  void persistEventAutoSaveTest() throws Throwable {
    TestEvent testEvent = new TestEvent("testId", "title", "content");

    this.eventPublisher.publishAtLeastOnce(testEvent);

    verify(this.persistEventAspect, times(1)).autoSaveEvent(any());
  }

  @Transactional
  @Rollback(value = false)
  @Test
  void afterEventTestFail() {
    TestEvent testEvent = new TestEvent("testId", "title", "content");

    this.eventPublisher.publicEvent(testEvent);

    verify(this.eventConverter, never()).toOutboxEvent(any());
  }
}
