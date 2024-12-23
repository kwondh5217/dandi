package com.e205.repository;

import com.e205.entity.FoundComment;
import com.e205.entity.LostComment;
import com.e205.base.item.query.CommentQuery;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {

  void save(FoundComment comment);

  void save(LostComment comment);

  Optional<FoundComment> findFoundComment(CommentQuery query);

  Optional<LostComment> findLostComment(CommentQuery query);

  List<FoundComment> findFoundComments(CommentQueryCondition condition);

  List<LostComment> findLostComments(CommentQueryCondition condition);
}
