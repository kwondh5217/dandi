package com.e205.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TestEventListener {

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(TestEvent event) {
    System.out.println("handle : " + event.getType());
  }
}
