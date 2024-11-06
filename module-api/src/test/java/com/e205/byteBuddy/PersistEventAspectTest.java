package com.e205.byteBuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.event.TestEvent;
import com.e205.event.TestEventService;
import com.e205.log.TransactionSynchronizationRegistryImpl;
import java.time.LocalDateTime;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.support.TransactionSynchronization;

@SpringBootTest
class PersistEventAspectTest {

  @Autowired
  private TestEventService testEventService;
  @SpyBean
  private PersistEventAspect persistEventAspect;
  @SpyBean
  private EventConverter eventConverter;
  @MockBean
  private TransactionSynchronizationRegistryImpl transactionSynchronizationRegistry;
  private LogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    this.logCaptor = LogCaptor.forClass(PersistEventAspect.class);
  }

  @Test
  void persistEventAutoSaveTest() throws Throwable {
    TestEvent testEvent = new TestEvent("testId", "title", "content");
    doNothing().when(transactionSynchronizationRegistry)
        .registerSynchronization(any(TransactionSynchronization.class));

    this.testEventService.save(testEvent);

    verify(this.persistEventAspect, times(1)).autoSaveEvent(any());
    verify(this.transactionSynchronizationRegistry, times(1))
        .registerSynchronization(any());
    assertThat(this.logCaptor.getInfoLogs().get(0).contains("PENDING")).isTrue();
  }

  @Test
  void afterEventTest() {
    TestEvent testEvent = new TestEvent("testId", "title", "content");
    OutboxEvent outboxEvent = new OutboxEvent("testId", EventStatus.PENDING, "payload",
        LocalDateTime.now(), "testEvent");
    given(this.eventConverter.toOutboxEvent(testEvent)).willReturn(outboxEvent);

    this.testEventService.save(testEvent);

    verify(this.eventConverter).toOutboxEvent(any());
    assertThat(EventStatus.COMPLETED).isEqualTo(outboxEvent.getStatus());
    assertThat(this.logCaptor.getInfoLogs().get(0).contains("COMPLETE")).isTrue();
  }
}
