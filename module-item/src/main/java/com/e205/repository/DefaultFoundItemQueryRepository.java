package com.e205.repository;

import static com.e205.entity.QFoundItem.foundItem;
import static com.e205.entity.QQuizSolver.quizSolver;

import com.e205.entity.FoundItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultFoundItemQueryRepository implements FoundItemQueryRepository {

  private final JPAQueryFactory queryFactory;

  public DefaultFoundItemQueryRepository(EntityManager em) {
    queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<FoundItem> findAllByMemberId(Integer memberId) {
    return queryFactory.selectFrom(foundItem)
        .where(foundItem.endedAt.isNull(), foundItem.memberId.eq(memberId))
        .fetch();
  }

  @Override
  public Optional<FoundItem> findById(Integer foundId) {
    return queryFactory.selectFrom(foundItem)
        .where(foundItem.id.eq(foundId))
        .stream().findAny();
  }

  @Override
  public List<FoundItem> findReadable(Integer memberId) {
    BooleanExpression exists = JPAExpressions
        .selectOne()
        .from(quizSolver)
        .where(quizSolver.memberId.eq(memberId),
            quizSolver.quiz.foundItem.eq(foundItem),
            quizSolver.solved.isTrue())
        .exists();

    return queryFactory.selectFrom(foundItem)
        .where(exists, foundItem.endedAt.isNull())
        .fetch();
  }
}
