package com.e205.member.service;

import com.e205.byteBuddy.EventConverter;
import com.e205.byteBuddy.EventStatus;
import com.e205.byteBuddy.OutboxEvent;
import com.e205.byteBuddy.OutboxEventRepository;
import com.e205.exception.GlobalException;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecoveryService {

  private final OutboxEventRepository outboxEventRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final EventConverter eventConverter;

  public void recovery(String eventId) {
    Assert.notNull(eventId, "eventId must not be null");

    OutboxEvent outboxEvent = this.outboxEventRepository.findById(eventId)
        .orElseThrow(() -> new GlobalException("E009"));

    Assert.state(outboxEvent.getStatus().equals(EventStatus.PENDING),
        "진행 중이지 않은 이벤트입니다. EventId: " + eventId);

    this.eventPublisher.publishEvent(this.eventConverter.toEvent(outboxEvent));
  }
}
