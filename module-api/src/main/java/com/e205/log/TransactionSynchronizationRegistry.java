package com.e205.log;

import org.springframework.transaction.support.TransactionSynchronization;

public interface TransactionSynchronizationRegistry {

  void registerSynchronization(TransactionSynchronization synchronization);

}
