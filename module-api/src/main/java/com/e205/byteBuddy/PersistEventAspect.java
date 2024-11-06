package com.e205.byteBuddy;

import com.e205.events.Event;
import com.e205.log.TransactionSynchronizationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;

@RequiredArgsConstructor
@Slf4j
@Component
@Aspect
public class PersistEventAspect {

  private final EventConverter converter;
  private final OutboxEventRepository outboxEventRepository;
  private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;

  @Around("@annotation(com.e205.byteBuddy.PersistEvent)")
  public Object autoSaveEvent(ProceedingJoinPoint joinPoint) throws Throwable {
    for (Object arg : joinPoint.getArgs()) {
      if (arg instanceof Event event) {
        registerBeforeCommitAction(() -> {
          OutboxEvent outboxEvent = this.converter.toOutboxEvent(event);
          logOutboxEvent(outboxEvent);
          this.outboxEventRepository.save(outboxEvent);
        });
      }
    }
    return joinPoint.proceed();
  }

  @Transactional
  @After("@annotation(org.springframework.transaction.event.TransactionalEventListener) && args(object)")
  public void afterEvent(Object object) {
    if (object instanceof Event event) {
      try {
        OutboxEvent outboxEvent = this.converter.toOutboxEvent(event);
        if (outboxEvent.getStatus().equals(EventStatus.PENDING)) {
          outboxEvent.complete();
          logOutboxEvent(outboxEvent);
        }
      } catch (Exception e) {
        log.error("Error while completing OutboxEvent", e);
      }
    }
  }

  private void registerBeforeCommitAction(Runnable action) {
    transactionSynchronizationRegistry.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void beforeCommit(boolean readOnly) {
            action.run();
          }
        });
  }

  private void logOutboxEvent(OutboxEvent outboxEvent) {
    log.info("Event - eventId: {}, status: {}, payload: {}, time: {}, eventType: {}",
        outboxEvent.getEventId(),
        outboxEvent.getStatus(),
        outboxEvent.getPayload(),
        outboxEvent.getTime(),
        outboxEvent.getEventType()
    );
  }
}
