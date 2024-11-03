package com.e205.config;

import com.e205.log.CollectionListener;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class JpaNPlusOneListenerConfig implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  public void registerListeners() {
    EntityManagerFactory entityManagerFactory = getEntityManagerFactory();

    if (entityManagerFactory != null) {
      registerCollectionListener(entityManagerFactory);
    }
  }

  private EntityManagerFactory getEntityManagerFactory() {
    return applicationContext.getBean(LocalContainerEntityManagerFactoryBean.class)
        .getObject();
  }

  private void registerCollectionListener(EntityManagerFactory entityManagerFactory) {
    SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(
        SessionFactoryImpl.class);

    EventListenerRegistry registry = sessionFactory.getEventEngine()
        .getListenerRegistry();
    registry.getEventListenerGroup(EventType.INIT_COLLECTION)
        .appendListener(new CollectionListener());
  }
}
