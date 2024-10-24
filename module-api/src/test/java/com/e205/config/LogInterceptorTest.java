package com.e205.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.e205.log.LoggableEntity;
import com.e205.log.TransactionSynchronizationRegistry;
import org.hibernate.type.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;

@ExtendWith(MockitoExtension.class)
class LogInterceptorTest {

  @Test
  void onFlushDirtyWhenEntityIsLoggableEntity() {
    // given
    var registry = mock(TransactionSynchronizationRegistry.class);
    LogInterceptor logInterceptor = new LogInterceptor(registry);
    LoggableEntity entity = mock(LoggableEntity.class);

    // when
    logInterceptor.onFlushDirty(entity, new Object(), new Object[]{}, new Object[]{},
        new String[]{}, new Type[]{});

    // then
    verify(registry).registerSynchronization(any(TransactionSynchronization.class));
  }

  @Test
  void onFlushDirtyWhenEntityIsNotLoggableEntity() {
    // given
    var registry = mock(TransactionSynchronizationRegistry.class);
    LogInterceptor logInterceptor = new LogInterceptor(registry);
    Object entity = mock(Object.class);

    // when
    logInterceptor.onFlushDirty(entity, new Object(), new Object[]{}, new Object[]{},
        new String[]{}, new Type[]{});

    // then
    verify(registry, never()).registerSynchronization(
        any(TransactionSynchronization.class));
  }

  @Test
  void onSaveWhenEntityIsLoggableEntity() {
    // given
    var registry = mock(TransactionSynchronizationRegistry.class);
    LogInterceptor logInterceptor = new LogInterceptor(registry);
    LoggableEntity entity = mock(LoggableEntity.class);

    // when
    logInterceptor.onSave(entity, new Object(), new Object[]{}, new String[]{},
        new Type[]{});

    // then
    verify(registry).registerSynchronization(any(TransactionSynchronization.class));
  }

  @Test
  void onSaveWhenEntityIsNotLoggableEntity() {
    // given
    var registry = mock(TransactionSynchronizationRegistry.class);
    LogInterceptor logInterceptor = new LogInterceptor(registry);
    Object entity = mock(Object.class);

    // when
    logInterceptor.onSave(entity, new Object(), new Object[]{}, new String[]{},
        new Type[]{});

    // then
    verify(registry, never()).registerSynchronization(
        any(TransactionSynchronization.class));
  }

  @Test
  void onDeleteWhenEntityIsLoggableEntity() {
    // given
    var registry = mock(TransactionSynchronizationRegistry.class);
    LogInterceptor logInterceptor = new LogInterceptor(registry);
    LoggableEntity entity = mock(LoggableEntity.class);

    // when
    logInterceptor.onDelete(entity, new Object(), new Object[]{}, new String[]{},
        new Type[]{});

    // then
    verify(registry).registerSynchronization(any(TransactionSynchronization.class));
  }

  @Test
  void onDeleteWhenEntityIsNotLoggableEntity() {
    // given
    var registry = mock(TransactionSynchronizationRegistry.class);
    LogInterceptor logInterceptor = new LogInterceptor(registry);
    Object entity = mock(Object.class);

    // when
    logInterceptor.onDelete(entity, new Object(), new Object[]{}, new String[]{},
        new Type[]{});

    // then
    verify(registry, never()).registerSynchronization(
        any(TransactionSynchronization.class));
  }
}