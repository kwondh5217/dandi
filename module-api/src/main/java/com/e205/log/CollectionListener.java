package com.e205.log;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.InitializeCollectionEvent;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.persister.collection.CollectionPersister;

@Slf4j
public class CollectionListener implements InitializeCollectionEventListener {

  @Override
  public void onInitializeCollection(InitializeCollectionEvent event) {
    PersistentCollection<?> collection = event.getCollection();
    Session session = event.getSession();
    SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) session.getFactory();
    CollectionPersister persister = sessionFactory.getMappingMetamodel()
        .getCollectionDescriptor(collection.getRole());

    if (persister.isLazy()) {
      log.info("Lazy loading detected for collection: " + persister.getRole());
    }
  }
}
