package com.e205.log;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;

@Slf4j
public class LazyLoadingInterceptor extends EmptyInterceptor {

  @Override
  public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
    if (entity instanceof HibernateProxy) {
      HibernateProxy proxy = (HibernateProxy) entity;
      if (!proxy.getHibernateLazyInitializer().isUninitialized()) {
        log.info("Lazy loading detected for field: {}", entity.getClass().getName());
      }
    }
    return super.onLoad(entity, id, state, propertyNames, types);
  }
}