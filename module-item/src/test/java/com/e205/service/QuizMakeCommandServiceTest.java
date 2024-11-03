package com.e205.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.FoundItemType;
import com.e205.command.QuizMakeCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.entity.Quiz;
import com.e205.entity.QuizImage;
import com.e205.repository.FoundItemQueryRepository;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.QuizCommandRepository;
import com.e205.repository.QuizImageRepository;
import com.e205.repository.QuizQueryRepository;
import com.e205.repository.QuizSolverRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 퀴즈 생성에 대한 테스트
 */
class QuizMakeCommandServiceTest {

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
    solverRepository = mock(QuizSolverRepository.class);

    service = new DefaultQuizCommandService(repository, imageRepository, itemImageRepository,
        foundItemQueryRepository, queryRepository, solverRepository);
  }

  @DisplayName("존재하는 습득물의 이미지가 20개가 안 될 경우, 퀴즈를 생성할 수 없다.")
  @Test
  void When_FoundItemImageLessThan20_Then_FailToMakeQuiz() {
    // given
    registerFoundImages(15);
    FoundItem foundItem = new FoundItem(1, 1d, 1d, "묘사", "저장", FoundItemType.OTHER,
        LocalDateTime.now());
    given(foundItemQueryRepository.findById(1)).willReturn(Optional.of(foundItem));
    given(itemImageRepository.findFoundImageById(any())).willReturn(Optional.of(mock()));

    var command = new QuizMakeCommand(1, 1, mock());

    // when
    ThrowingCallable expectThrow = () -> service.make(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("습득물의 이미지가 부족해서 퀴즈를 낼 수 없습니다.");
  }

  @DisplayName("타겟 습득물이 존재하지 않으면, 퀴즈를 생성할 수 없다.")
  @Test
  void When_TargetItemNotExist_Then_FailToMakeQuiz() {
    // given
    registerFoundImages(100);

    given(foundItemQueryRepository.findById(any())).willReturn(Optional.empty());

    var command = new QuizMakeCommand(1, 1, mock());
    // when
    ThrowingCallable expectThrow = () -> service.make(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("습득물이 존재하지 않습니다.");
  }


  @DisplayName("타겟 습득물의 주인이 아니면, 퀴즈를 생성할 수 없다.")
  @Test
  void When_IsNotOwner_Then_FailToMakeQuiz() {
    // given
    registerFoundImages(100);
    Integer foundItemId = 1;
    Integer memberId = 1;
    FoundItem foundItem = new FoundItem(2, 1d, 1d, "묘사", "저장", FoundItemType.OTHER,
        LocalDateTime.now());
    given(foundItemQueryRepository.findById(foundItemId)).willReturn(Optional.of(foundItem));
    QuizMakeCommand command = new QuizMakeCommand(foundItemId, memberId, mock());

    // when
    ThrowingCallable expectThrow = () -> service.make(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("퀴즈를 생성할 권한이 없습니다.");
  }

  @DisplayName("퀴즈의 정답 사진이 존재하지 않으면, 퀴즈를 생성할 수 없다.")
  @Test
  void When_QuizAnswerImageIsNotExist_Then_FailToMakeImage() {
    // given
    registerFoundImages(100);
    FoundItem foundItem = new FoundItem(1, 1d, 1d, "묘사", "저장", FoundItemType.OTHER,
        LocalDateTime.now());
    given(foundItemQueryRepository.findById(any())).willReturn(Optional.of(foundItem));

    given(itemImageRepository.findFoundImageById(any())).willReturn(Optional.empty());

    QuizMakeCommand command = new QuizMakeCommand(1, 1, mock());

    // when
    ThrowingCallable expectThrow = () -> service.make(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("이미지가 존재하지 않습니다.");
  }

  @DisplayName("정상적인 입력이면, 퀴즈가 잘 생성된다.")
  @Test
  void When_CommandIsValid_Then_SuccessToMakeQuiz() {
    // given
    registerFoundImages(100);
    Integer foundItemId = 1;
    Integer memberId = 1;
    int optionCount = 4;

    FoundItem foundItem = new FoundItem(1, 1d, 1d, "묘사", "저장", FoundItemType.OTHER,
        LocalDateTime.now());
    given(foundItemQueryRepository.findById(foundItemId)).willReturn(Optional.of(foundItem));

    UUID answerImage = UUID.randomUUID();
    given(itemImageRepository.findFoundImageById(answerImage)).willReturn(mock());


    QuizMakeCommand command = new QuizMakeCommand(foundItemId, memberId, answerImage);

    // when
    service.make(command);

    // then
    verify(repository).save(any(Quiz.class));
    verify(imageRepository, times(optionCount)).save(any(QuizImage.class));
  }

  private void registerFoundImages(Integer count) {
    List<FoundImage> foundImages = Stream.generate(
            () -> new FoundImage(UUID.randomUUID(), "png", mock()))
        .limit(count)
        .toList();
    given(itemImageRepository.findTopFoundImagesByCreateAtDesc(any())).willReturn(foundImages);
  }
}
