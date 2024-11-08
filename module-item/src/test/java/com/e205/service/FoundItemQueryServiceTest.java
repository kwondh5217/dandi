package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizSolver;
import com.e205.payload.FoundItemPayload;
import com.e205.query.FoundItemQuery;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizSolverRepository;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FoundItemQueryServiceTest {

  private static final Integer memberId = 1;
  private static final Integer solverId = 2;
  private static final Integer foundId = 10;

  FoundItemQueryService service;
  QuizSolverRepository solverRepository;
  ItemImageRepository imageRepository;
  FoundItemQueryRepository foundItemQueryRepository;

  @BeforeEach
  void setUp() {
    solverRepository = mock(QuizSolverRepository.class);
    imageRepository = mock(ItemImageRepository.class);
    foundItemQueryRepository = mock(FoundItemQueryRepository.class);

    service = new DefaultFoundItemQueryService(solverRepository, imageRepository, foundItemQueryRepository);
  }

  @DisplayName("퀴즈를 풀지 않았으면 습득물을 조회할 수 없다.")
  @Test
  void When_NotSolveQuiz_Then_FailToGetFoundItem() {
    // given
    given(solverRepository.findByMemberIdAndFoundId(solverId, foundId)).willReturn(
        Optional.empty());

    FoundItemQuery query = new FoundItemQuery(solverId, foundId);

    // when
    ThrowingCallable expectThrow = () -> service.find(query);

    // then
    assertThatThrownBy(expectThrow).hasMessage("퀴즈를 풀지 않았습니다.");
  }

  @DisplayName("퀴즈를 틀렸으면 습득물을 조회할 수 없다.")
  @Test
  void When_FailToSolveQuiz_Then_FailToGetFoundItem() {
    // given
    QuizSolver solver = new QuizSolver(mock(), solverId, false);
    given(solverRepository.findByMemberIdAndFoundId(solverId, foundId)).willReturn(
        Optional.of(solver));

    FoundItemQuery query = new FoundItemQuery(solverId, foundId);

    // when
    ThrowingCallable expectThrow = () -> service.find(query);

    // then
    assertThatThrownBy(expectThrow).hasMessage("습득물을 조회할 권한이 없습니다.");
  }

  @DisplayName("퀴즈를 푼 사람은 습득물을 조회할 수 있다.")
  @Test
  void When_Solver_Then_SuccessToGetFoundItem() {
    // given
    FoundItem foundItem = mock(FoundItem.class);
    FoundItemPayload mockPayload = mock(FoundItemPayload.class);
    given(foundItem.toPayload()).willReturn(mockPayload);

    Quiz quiz = mock(Quiz.class);
    given(quiz.getFoundItem()).willReturn(foundItem);

    QuizSolver solver = new QuizSolver(quiz, memberId, true);
    given(solverRepository.findByMemberIdAndFoundId(solverId, foundId)).willReturn(
        Optional.of(solver));

    FoundItemQuery query = new FoundItemQuery(solverId, foundId);

    // when
    FoundItemPayload result = service.find(query);

    // then
    assertThat(result).isEqualTo(mockPayload);
  }
}