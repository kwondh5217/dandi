package com.e205.repository;

import static com.e205.entity.QFoundComment.foundComment;
import static com.e205.entity.QLostComment.lostComment;

import com.e205.CommentType;
import com.e205.entity.Comment;
import com.e205.entity.FoundComment;
import com.e205.entity.LostComment;
import com.e205.entity.QComment;
import com.e205.entity.QFoundComment;
import com.e205.entity.QLostComment;
import com.e205.query.CommentQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultCommentRepository implements CommentRepository {

  private final EntityManager em;
  private final JPAQueryFactory queryFactory;

  public DefaultCommentRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public void save(FoundComment comment) {
    em.persist(comment);
  }

  @Override
  public void save(LostComment comment) {
    em.persist(comment);
  }

  @Override
  public Optional<FoundComment> findFoundComment(CommentQuery query) {
    QFoundComment parent = new QFoundComment("parent");
    return queryFactory.selectFrom(foundComment)
        .leftJoin(foundComment.parent, parent).fetchJoin()
        .where(queryToCondition(query))
        .stream().findAny();
  }

  @Override
  public Optional<LostComment> findLostComment(CommentQuery query) {
    QLostComment parent = new QLostComment("parent");
    return queryFactory.selectFrom(lostComment)
        .leftJoin(lostComment.parent, parent).fetchJoin()
        .where(queryToCondition(query))
        .stream().findAny();
  }

  @Override
  public List<FoundComment> findFoundComments(CommentQueryCondition condition) {
    BooleanBuilder builder = queryToCondition(
        new CommentQuery(condition.writerId(), CommentType.FOUND, condition.itemId(),
            condition.parentId(), null));

    if (!condition.fetchAll()) {
      if (condition.parentId() == null) {
        builder.and(foundComment.parent.id.isNull());
      } else {
        builder.and(foundComment.parent.id.eq(condition.parentId()));
      }
    }

    if (condition.cursor() != null) {
      Comment cursorComment = queryFactory
          .selectFrom(foundComment)
          .where(foundComment.id.eq(condition.cursor()))
          .fetchOne();

      if (cursorComment != null) {
        builder.and(foundComment.createdAt.lt(cursorComment.getCreatedAt()));
      }
    }

    QFoundComment parent = new QFoundComment("parent");
    return queryFactory
        .selectFrom(foundComment)
        .leftJoin(foundComment.parent, parent).fetchJoin()
        .where(builder)
        .orderBy(foundComment.createdAt.desc())
        .limit(condition.limit())
        .fetch();
  }

  @Override
  public List<LostComment> findLostComments(CommentQueryCondition condition) {
    BooleanBuilder builder = queryToCondition(
        new CommentQuery(condition.writerId(), CommentType.LOST, condition.itemId(),
            condition.parentId(), null));

    if (!condition.fetchAll()) {
      if (condition.parentId() == null) {
        builder.and(lostComment.parent.id.isNull());
      } else {
        builder.and(lostComment.parent.id.eq(condition.parentId()));
      }
    }

    if (condition.cursor() != null) {
      Comment cursorComment = queryFactory
          .selectFrom(lostComment)
          .where(lostComment.id.eq(condition.cursor()))
          .fetchOne();

      if (cursorComment != null) {
        builder.and(lostComment.createdAt.lt(cursorComment.getCreatedAt()));
      }
    }

    QLostComment parent = new QLostComment("parent");
    return queryFactory
        .selectFrom(lostComment)
        .leftJoin(lostComment.parent, parent).fetchJoin()
        .where(builder)
        .orderBy(lostComment.createdAt.desc())
        .limit(condition.limit())
        .fetch();
  }

  private BooleanBuilder queryToCondition(CommentQuery query) {
    BooleanBuilder builder = new BooleanBuilder();
    switch (query.type()) {
      case LOST -> {
        if (query.writerId() != null) {
          builder.and(lostComment.writerId.eq(query.writerId()));
        }
        if (query.itemId() != null) {
          builder.and(lostComment.lostItem.id.eq(query.itemId()));
        }
        if (query.commentId() != null) {
          builder.and(lostComment.id.eq(query.commentId()));
        }
        if (query.parentId() != null) {
          builder.and(lostComment.parent.id.eq(query.parentId()));
        }
      }
      case FOUND -> {
        if (query.writerId() != null) {
          builder.and(foundComment.writerId.eq(query.writerId()));
        }
        if (query.itemId() != null) {
          builder.and(foundComment.foundItem.id.eq(query.itemId()));
        }
        if (query.commentId() != null) {
          builder.and(foundComment.id.eq(query.commentId()));
        }
        if (query.parentId() != null) {
          builder.and(foundComment.parent.id.eq(query.parentId()));
        }
      }
    }
    return builder;
  }
}
