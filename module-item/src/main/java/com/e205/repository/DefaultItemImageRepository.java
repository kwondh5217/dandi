package com.e205.repository;

import static com.e205.entity.QFoundImage.foundImage;
import static com.e205.entity.QLostImage.lostImage;

import com.e205.entity.FoundImage;
import com.e205.entity.LostImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

  @Override
  public Optional<FoundImage> findByFoundItemId(Integer foundItemId) {
    return queryFactory.selectFrom(foundImage)
        .where(foundImage.foundItem.id.eq(foundItemId))
        .stream().findAny();
  }

  @Override
  public List<FoundImage> findTopFoundImagesByCreateAtDesc(Integer count) {
    return queryFactory.selectFrom(foundImage)
        .orderBy(foundImage.createdAt.desc())
        .limit(count)
        .fetch();
  }

  @Override
  public Optional<LostImage> findLostImageById(UUID id) {
    return queryFactory.selectFrom(lostImage)
        .where(lostImage.id.eq(id))
        .stream().findFirst();
  }

  @Override
  public Optional<FoundImage> findFoundImageById(UUID id) {
    return queryFactory.selectFrom(foundImage)
        .where(foundImage.id.eq(id))
        .stream().findFirst();
  }
}
