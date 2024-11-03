package com.e205.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizSolver;
import com.e205.query.QuizQuery;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.QuizImageRepository;
import com.e205.repository.QuizQueryRepository;
import com.e205.repository.QuizSolverRepository;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuizQueryServiceTest {

  static Integer memberId = 1;
  static Integer foundId = 1;
  static Integer quizId = 1;

  QuizQueryService service;
  QuizSolverRepository solverRepository;
  FoundItemQueryRepository foundRepository;
  QuizQueryRepository quizRepository;
  QuizImageRepository quizImageRepository;

  @BeforeEach
  void setUp() {
    solverRepository = mock(QuizSolverRepository.class);
    foundRepository = mock(FoundItemQueryRepository.class);
    quizRepository = mock(QuizQueryRepository.class);

    given(foundRepository.findById(any())).willReturn(Optional.of(mock()));

    service = new DefaultQuizQueryService(foundRepository, solverRepository, quizRepository,
        quizImageRepository);
  }

  @DisplayName("습득물이 존재하지 않으면, 퀴즈를 조회할 수 없다.")
  @Test
  void When_FoundIsNotExist_Then_FailToQuery() {
    // given
    given(foundRepository.findById(any())).willReturn(Optional.empty());

    QuizQuery query = new QuizQuery(memberId, foundId);

    // when
    ThrowingCallable expectThrow = () -> service.findQuiz(query);

    // then
    assertThatThrownBy(expectThrow).hasMessage("습득물이 존재하지 않습니다.");
  }

  @DisplayName("퀴즈를 풀었으면, 퀴즈를 조회할 수 없다.")
  @Test
  void When_AlreadySolved_Then_FailToQuery() {
    // given
    QuizSolver quizSolver = new QuizSolver(mock(), memberId, false);
    Quiz quiz = mock();
    given(quiz.getId()).willReturn(quizId);
    given(quizRepository.findByFoundItemId(any())).willReturn(Optional.of(quiz));
    given(solverRepository.findByMemberIdAndQuizId(memberId, quizId)).willReturn(
        Optional.of(quizSolver));

    QuizQuery query = new QuizQuery(memberId, foundId);

    // when
    ThrowingCallable expectThrow = () -> service.findQuiz(query);

    // then
    assertThatThrownBy(expectThrow).hasMessage("이미 퀴즈를 풀었습니다.");
  }

  @DisplayName("퀴즈가 존재하지 않으면, 퀴즈를 조회할 수 없다.")
  @Test
  void When_QuizIsNotExist_Then_FailToQuery() {
    // given
    given(quizRepository.findByFoundItemId(foundId)).willReturn(Optional.empty());

    QuizQuery query = new QuizQuery(memberId, foundId);

    // when
    ThrowingCallable expectThrow = () -> service.findQuiz(query);

    // then
    assertThatThrownBy(expectThrow).hasMessage("퀴즈가 존재하지 않습니다.");
  }

  @DisplayName("습득물을 등록한 사람은 퀴즈를 조회할 수 없다.")
  @Test
  void When_FoundOwner_Then_FailToQuery() {
    // given
    FoundItem foundItem = mock();
    given(foundItem.getMemberId()).willReturn(memberId);
    given(quizRepository.findById(any())).willReturn(Optional.of(mock()));
    given(foundRepository.findById(foundId)).willReturn(Optional.of(foundItem));

    QuizQuery query = new QuizQuery(memberId, foundId);

    // when
    ThrowingCallable expectThrow = () -> service.findQuiz(query);

    // then
    assertThatThrownBy(expectThrow).hasMessage("습득물을 등록한 사람은 퀴즈를 풀 수 없습니다.");
  }
}