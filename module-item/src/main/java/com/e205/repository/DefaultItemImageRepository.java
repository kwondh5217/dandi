package com.e205.repository;

import static com.e205.entity.QLostImage.lostImage;

import com.e205.entity.FoundImage;
import com.e205.entity.LostImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DefaultItemImageRepository implements ItemImageRepository {

  private final JPAQueryFactory queryFactory;
  private final EntityManager entityManager;

  public DefaultItemImageRepository(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
    this.entityManager = em;
  }

  @Override
  @Transactional
  public LostImage save(LostImage lostImage) {
    entityManager.persist(lostImage);
    return lostImage;
  }

  @Override
  public FoundImage save(FoundImage foundImage) {
    entityManager.persist(foundImage);
    return foundImage;
  }

  @Override
  public List<LostImage> findAllByLostItemId(Integer lostItemId) {
    return queryFactory.selectFrom(lostImage)
        .where(lostImage.lostItem.id.eq(lostItemId))
        .fetch();
  }
}
