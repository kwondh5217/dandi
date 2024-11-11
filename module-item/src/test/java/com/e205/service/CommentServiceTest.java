package com.e205.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.e205.CommentType;
import com.e205.command.CommentCreateCommand;
import com.e205.query.CommentQuery;
import com.e205.repository.CommentRepository;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.LostItemRepository;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentServiceTest {

  DefaultCommentService commentService;
  CommentRepository commentRepository;
  FoundItemQueryRepository foundItemRepository;
  LostItemRepository lostItemRepository;

  static final int writerId = 1;
  static final int itemId = 2;
  static final String content = "댓글 내용";

  @BeforeEach
  void setUp() {
    commentRepository = mock(CommentRepository.class);
    foundItemRepository = mock(FoundItemQueryRepository.class);
    lostItemRepository = mock(LostItemRepository.class);
    commentService = new DefaultCommentService(commentRepository, foundItemRepository,
        lostItemRepository);
  }

  @DisplayName("댓글을 생성할 때 분실물이 존재하지 않으면 예외가 발생한다.")
  @Test
  void When_CreateCommentWithNoItem_Then_ThrowException() {
    // given
    given(lostItemRepository.findById(itemId)).willReturn(Optional.empty());

    CommentCreateCommand command = new CommentCreateCommand(writerId, itemId, CommentType.LOST,
        null, content);

    // when
    ThrowingCallable expectThrow = () -> commentService.createComment(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("분실물이 존재하지 않습니다.");
  }

  @DisplayName("대댓글을 생성할 때 부모 댓글이 존재하지 않으면 예외가 발생한다.")
  @Test
  void When_CreateCommentWithNotExistParent_Then_ThrowException() {
    // given
    int parentId = 1;
    given(commentRepository.findLostComment(
        new CommentQuery(writerId, CommentType.LOST, itemId, null, parentId))).willReturn(Optional.empty());
    given(lostItemRepository.findById(itemId)).willReturn(Optional.of(mock()));

    CommentCreateCommand command = new CommentCreateCommand(writerId, itemId, CommentType.LOST,
        1, content);

    // when
    ThrowingCallable expectThrow = () -> commentService.createComment(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("댓글이 존재하지 않습니다.");
  }
}