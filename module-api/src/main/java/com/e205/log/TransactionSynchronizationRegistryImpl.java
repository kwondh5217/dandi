package com.e205.log;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Component
public class TransactionSynchronizationRegistryImpl implements
    TransactionSynchronizationRegistry {

  @Override
  public void registerSynchronization(TransactionSynchronization synchronization) {
    TransactionSynchronizationManager.registerSynchronization(synchronization);
  }
}
