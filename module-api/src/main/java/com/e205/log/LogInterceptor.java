package com.e205.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionSynchronization;

@RequiredArgsConstructor
@Slf4j
public class LogInterceptor implements Interceptor, ApplicationContextAware {

  private ApplicationContext applicationContext;
  private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames,
      Type[] types)
      throws CallbackException {
    if (entity instanceof HibernateProxy proxy) {
      if(!proxy.getHibernateLazyInitializer().isUninitialized()) {
        log.info("Lazy loading detected for field: {}", entity.getClass().getName());
      }
    }
    return false;
  }

  private ObjectMapper getObjectMapper() {
    return applicationContext.getBean(ObjectMapper.class);
  }

  @Override
  public boolean onFlushDirty(Object entity, Object id, Object[] currentState,
      Object[] previousState,
      String[] propertyNames, Type[] types) throws CallbackException {
    if (entity instanceof LoggableEntity) {
      registerAfterCommitAction(
          () -> logEntityUpdate(entity, id, propertyNames, previousState, currentState));
    }
    return false;
  }

  @Override
  public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames,
      Type[] types)
      throws CallbackException {
    if (entity instanceof LoggableEntity) {
      registerAfterCommitAction(() -> logEntitySave(entity));
    }
    return false;
  }

  @Override
  public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames,
      Type[] types)
      throws CallbackException {
    if (entity instanceof LoggableEntity) {
      registerAfterCommitAction(() -> logEntityDelete(entity));
    }
  }

  private void registerAfterCommitAction(Runnable action) {
    transactionSynchronizationRegistry.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        action.run();
      }
    });
  }

  private void logEntityUpdate(Object entity, Object id, String[] propertyNames,
      Object[] previousState,
      Object[] currentState) {
    var before = getLoggableEntitySnapshot(entity, id, propertyNames, previousState);
    var after = getLoggableEntitySnapshot(entity, id, propertyNames, currentState);
    logChange("Update", before, after, entity.getClass().getName());
  }

  private void logEntitySave(Object entity) {
    var save = (LoggableEntity) entity;
    logChange("Save", save, entity.getClass().getName());
  }

  private void logEntityDelete(Object entity) {
    var delete = (LoggableEntity) entity;
    logChange("Delete", delete, entity.getClass().getName());
  }

  private void logChange(String action, Object before, Object after, String className) {
    try {
      ObjectMapper objectMapper = getObjectMapper();
      log.info("{} for entity: before={}, after={}, class: {}", action,
          objectMapper.writeValueAsString(before), objectMapper.writeValueAsString(after),
          className);
    } catch (JsonProcessingException e) {
      log.warn("Failed to serialize {} log for class {}", action, className, e);
    }
  }

  private void logChange(String action, LoggableEntity entity, String className) {
    try {
      ObjectMapper objectMapper = getObjectMapper();
      log.info("{} for entity: {}, class: {}", action, objectMapper.writeValueAsString(entity),
          className);
    } catch (JsonProcessingException e) {
      log.warn("Failed to serialize {} log for class {}", action, className, e);
    }
  }

  private LoggableEntity getLoggableEntitySnapshot(Object entity, Object id, String[] propertyNames,
      Object[] stateValues) {
    try {
      LoggableEntity snapshot = (LoggableEntity) entity.getClass().getDeclaredConstructor()
          .newInstance();
      setField(snapshot, "id", id);

      for (int i = 0; i < propertyNames.length; i++) {
        setField(snapshot, propertyNames[i], stateValues[i]);
      }
      return snapshot;
    } catch (Exception e) {
      log.warn("Failed to create loggable entity snapshot for class {}. Error: {}",
          entity.getClass().getName(), e.getMessage(), e);
      return null;
    }
  }

  private void setField(Object target, String fieldName, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
