package com.e205.member.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.byteBuddy.EventConverter;
import com.e205.byteBuddy.EventStatus;
import com.e205.byteBuddy.OutboxEvent;
import com.e205.byteBuddy.OutboxEventRepository;
import com.e205.events.Event;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class RecoveryServiceTest {

  private RecoveryService recoveryService;
  private OutboxEventRepository outboxEventRepository;
  private ApplicationEventPublisher eventPublisher;
  private EventConverter eventConverter;

  @BeforeEach
  void setUp() {
    this.outboxEventRepository = mock(OutboxEventRepository.class);
    this.eventPublisher = mock(ApplicationEventPublisher.class);
    this.eventConverter = mock(EventConverter.class);
    this.recoveryService = new RecoveryService(outboxEventRepository, eventPublisher,
        eventConverter);
  }

  @Test
  void recoveryEvent() {
    OutboxEvent outboxEvent = mock(OutboxEvent.class);
    given(outboxEvent.getStatus()).willReturn(EventStatus.PENDING);
    given(this.outboxEventRepository.findById(any())).willReturn(
        Optional.of(outboxEvent));
    Event event = mock(Event.class);
    given(this.eventConverter.toEvent(any())).willReturn(event);

    assertDoesNotThrow(() -> this.recoveryService.recovery("1"));
    verify(this.eventPublisher).publishEvent(any(Event.class));
  }

  @Test
  void recoveryEvent_doesNotExistEventId() {
    assertThrows(RuntimeException.class, () -> this.recoveryService.recovery(null));
  }

  @Test
  void recoveryEvent_doesNotExist() {
    given(this.outboxEventRepository.findById(any())).willReturn(
        Optional.empty());

    assertThrows(RuntimeException.class, () -> this.recoveryService.recovery("1"));
  }

  @Test
  void recoveryEvent_isNotPending() {
    OutboxEvent outboxEvent = mock(OutboxEvent.class);
    given(outboxEvent.getStatus()).willReturn(EventStatus.COMPLETED);
    given(this.outboxEventRepository.findById(any())).willReturn(
        Optional.of(outboxEvent));

    assertThrows(IllegalStateException.class, () -> this.recoveryService.recovery("1"));
  }
}