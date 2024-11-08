package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.command.QuizSubmitCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.Quiz;
import com.e205.entity.QuizSolver;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizCommandRepository;
import com.e205.repository.QuizImageRepository;
import com.e205.repository.QuizQueryRepository;
import com.e205.repository.QuizSolverRepository;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * 퀴즈 풀기에 대한 테스트
 */
public class QuizSubmitCommandTest {

  QuizCommandService service;
  QuizCommandRepository repository;
  QuizImageRepository imageRepository;
  QuizQueryRepository queryRepository;
  QuizSolverRepository solverRepository;
  FoundItemQueryRepository foundItemQueryRepository;
  ItemImageRepository itemImageRepository;

  @BeforeEach
  void setUp() {
    repository = mock(QuizCommandRepository.class);
    imageRepository = mock(QuizImageRepository.class);
    foundItemQueryRepository = mock(FoundItemQueryRepository.class);
    itemImageRepository = mock(ItemImageRepository.class);
    queryRepository = mock(QuizQueryRepository.class);
    solverRepository  = mock(QuizSolverRepository.class);

    service = new DefaultQuizCommandService(repository, imageRepository, itemImageRepository,
        foundItemQueryRepository, queryRepository, solverRepository);
  }

  @DisplayName("이미 퀴즈를 풀었다면, 다시 풀 수 없다.")
  @Test
  void When_AlreadySolved_Then_CannotSubmit() {
    // given
    given(solverRepository.findByMemberIdAndQuizId(any(), any())).willReturn(Optional.of(mock()));

    QuizSubmitCommand command = new QuizSubmitCommand(1, 1, UUID.randomUUID());

    // when
    ThrowingCallable expectThrow = () -> service.submit(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("이미 퀴즈를 풀었습니다.");
  }

  @DisplayName("퀴즈가 존재하지 않으면 퀴즈를 풀 수 없다.")
  @Test
  void When_QuizIsNotExist_Then_CannotSubmit() {
    // given
    given(queryRepository.findById(any())).willReturn(Optional.empty());

    QuizSubmitCommand command = new QuizSubmitCommand(1, 1, UUID.randomUUID());

    // when
    ThrowingCallable expectThrow = () -> service.submit(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("퀴즈가 존재하지 않습니다.");
  }

  @DisplayName("퀴즈의 정답이 틀렸으면, solver에 틀렸다고 저장한다.")
  @Test
  void When_FailToSolve_Then_SaveFail() {
    // given
    UUID answerId = UUID.randomUUID();
    FoundImage answer = mock();
    given(answer.getId()).willReturn(answerId);

    Quiz quiz = new Quiz(mock(), answer);
    given(queryRepository.findById(any())).willReturn(Optional.of(quiz));

    ArgumentCaptor<QuizSolver> captor = ArgumentCaptor.forClass(QuizSolver.class);

    UUID notAnswerId = UUID.randomUUID();
    QuizSubmitCommand command = new QuizSubmitCommand(1, 1, notAnswerId);

    // when
    service.submit(command);

    // then
    verify(solverRepository).save(captor.capture());
    QuizSolver solver = captor.getValue();
    assertThat(solver.isSolved()).isFalse();
  }

  @DisplayName("퀴즈를 맞췄으면, 맞췄다고 저장한다.")
  @Test
  void When_SolveQuiz_Then_SaveSuccess() {
    // given
    UUID answerId = UUID.randomUUID();
    FoundImage answer = mock();
    given(answer.getId()).willReturn(answerId);

    Quiz quiz = new Quiz(mock(), answer);
    given(queryRepository.findById(any())).willReturn(Optional.of(quiz));

    ArgumentCaptor<QuizSolver> captor = ArgumentCaptor.forClass(QuizSolver.class);

    QuizSubmitCommand command = new QuizSubmitCommand(1, 1, answerId);

    // when
    service.submit(command);

    // then
    verify(solverRepository).save(captor.capture());
    QuizSolver solver = captor.getValue();
    assertThat(solver.isSolved()).isTrue();
  }
}
