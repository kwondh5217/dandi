package com.e205.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
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
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  private EntityManager getEntityManager() {
    return applicationContext.getBean(EntityManager.class);
  }

  private ObjectMapper getObjectMapper() {
    return applicationContext.getBean(ObjectMapper.class);
  }

  @Override
  public boolean onFlushDirty(Object entity, Object id, Object[] currentState,
      Object[] previousState, String[] propertyNames, Type[] types)
      throws CallbackException {
    if (!(entity instanceof LoggableEntity)) {
      return false;
    }

    transactionSynchronizationRegistry.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            EntityManager em = getEntityManager();
            ObjectMapper objectMapper = getObjectMapper();

            em.detach(entity);

            var before = getLoggableEntity(entity, id, propertyNames, previousState);
            var after = getLoggableEntity(entity, id, propertyNames, currentState);

            UpdateLog<LoggableEntity> updateLog = new UpdateLog<>(before, after);

            try {
              log.info("Update for entity: {}, class: {}",
                  objectMapper.writeValueAsString(updateLog),
                  entity.getClass().getName());
            } catch (JsonProcessingException e) {
              log.warn("Failed to serialize update log", e);
            }
          }
        }
    );
    return false;
  }

  @Override
  public boolean onSave(Object entity, Object id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException {
    if (!(entity instanceof LoggableEntity)) {
      return false;
    }

    transactionSynchronizationRegistry.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            try {
              ObjectMapper objectMapper = getObjectMapper();

              var save = (LoggableEntity) entity;
              SaveLog<LoggableEntity> saveLog = new SaveLog<>(save);
              log.info("Save for entity: {}, class: {}",
                  objectMapper.writeValueAsString(saveLog), entity.getClass().getName());
            } catch (JsonProcessingException e) {
              log.warn("Failed to serialize update log", e);
            }
          }
        });
    return false;
  }

  @Override
  public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames,
      Type[] types) throws CallbackException {
    if ((entity instanceof LoggableEntity)) {
      transactionSynchronizationRegistry.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              EntityManager em = getEntityManager();
              ObjectMapper objectMapper = getObjectMapper();

              var loggableEntity = (LoggableEntity) entity;
              em.detach(loggableEntity);
              DeleteLog<LoggableEntity> deleteLog = new DeleteLog<>(loggableEntity);
              try {
                log.info("Delete for entity: {}, class: {}",
                    objectMapper.writeValueAsString(deleteLog),
                    entity.getClass().getName());
              } catch (JsonProcessingException e) {
                log.warn("Failed to serialize update log", e);
              }
            }
          });
    }
  }

  private LoggableEntity getLoggableEntity(Object entity, Object id,
      String[] propertyNames,
      Object[] previousState) {
    try {
      Object object = entity.getClass()
          .getDeclaredConstructor().newInstance();
      Field idField = object.getClass().getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(object, id);

      for (int i = 0; i < propertyNames.length; i++) {
        String propertyName = propertyNames[i];
        Object propertyValue = previousState[i];

        Field declaredField = object.getClass().getDeclaredField(propertyName);
        declaredField.setAccessible(true);
        declaredField.set(object, propertyValue);
      }
      return (LoggableEntity) object;
    } catch (Exception e) {
      log.warn("Failed to create loggable entity for class: {}. Error: {}",
          entity.getClass().getName(), e.getMessage(), e);
      return null;
    }
  }
}

