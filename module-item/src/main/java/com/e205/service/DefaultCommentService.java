package com.e205.service;

import com.e205.CommentType;
import com.e205.command.CommentCreateCommand;
import com.e205.entity.FoundComment;
import com.e205.entity.FoundItem;
import com.e205.entity.LostComment;
import com.e205.entity.LostItem;
import com.e205.exception.ItemError;
import com.e205.payload.CommentPayload;
import com.e205.query.CommentListQuery;
import com.e205.query.CommentQuery;
import com.e205.repository.CommentQueryCondition;
import com.e205.repository.CommentRepository;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.LostItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DefaultCommentService implements CommentService {

  private final CommentRepository repository;
  private final FoundItemQueryRepository foundItemQueryRepository;
  private final LostItemRepository lostItemRepository;

  @Transactional
  @Override
  public void createComment(CommentCreateCommand command) {
    switch (command.commentType()) {
      case LOST -> createLostComment(command);
      case FOUND -> createFoundComment(command);
    }
  }

  @Transactional(readOnly = true)
  @Override
  public List<CommentPayload> findComments(CommentListQuery query) {
    return switch (query.type()) {
      case LOST -> repository.findLostComments(CommentQueryCondition.from(query))
          .stream().map(LostComment::toPayload).toList();
      case FOUND -> repository.findFoundComments(CommentQueryCondition.from(query))
          .stream().map(FoundComment::toPayload).toList();
    };
  }

  private void createLostComment(CommentCreateCommand command) {
    LostComment parent = null;
    if (command.parentId() != null) {
      parent = repository.findLostComment(
              new CommentQuery(null, CommentType.LOST, command.itemId(), null, command.parentId()))
          .orElseThrow(ItemError.COMMENT_NOT_EXIST::getGlobalException);
    }

    LostComment comment = LostComment.builder()
        .writerId(command.writerId())
        .content(command.content())
        .parent(parent)
        .lostItem(getLostItem(command.itemId()))
        .createdAt(LocalDateTime.now())
        .build();

    repository.save(comment);
  }

  private void createFoundComment(CommentCreateCommand command) {
    FoundComment parent = null;
    if (command.parentId() != null) {
      parent = repository.findFoundComment(
              new CommentQuery(null, CommentType.FOUND, command.itemId(), null, command.parentId()))
          .orElseThrow(ItemError.COMMENT_NOT_EXIST::getGlobalException);
    }

    FoundComment comment = FoundComment.builder()
        .writerId(command.writerId())
        .content(command.content())
        .parent(parent)
        .foundItem(getFoundItem(command.itemId()))
        .createdAt(LocalDateTime.now())
        .build();

    repository.save(comment);
  }

  private FoundItem getFoundItem(int foundItemId) {
    return foundItemQueryRepository.findById(foundItemId)
        .orElseThrow(ItemError.FOUND_NOT_EXIST::getGlobalException);
  }

  private LostItem getLostItem(int lostItemId) {
    return lostItemRepository.findById(lostItemId)
        .orElseThrow(ItemError.LOST_NOT_FOUND::getGlobalException);
  }

}
