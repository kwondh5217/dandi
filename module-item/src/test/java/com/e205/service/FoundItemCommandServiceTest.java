package com.e205.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import com.e205.command.QuizMakeCommand;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.event.FoundItemSaveEvent;
import com.e205.events.EventPublisher;
import com.e205.repository.FoundItemCommandRepository;
import com.e205.repository.ItemImageRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FoundItemCommandServiceTest {

  FoundItemCommandService service;
  FoundItemCommandRepository repository;
  ItemImageRepository imageRepository;
  QuizCommandService quizService;
  EventPublisher eventPublisher;

  @BeforeEach
  void setUp() {
    repository = mock(FoundItemCommandRepository.class);
    imageRepository = mock(ItemImageRepository.class);
    quizService = mock(QuizCommandService.class);
    eventPublisher = mock(EventPublisher.class);

    given(repository.save(any())).willAnswer(answer -> answer.getArgument(0));
    given(imageRepository.save((any(FoundImage.class)))).willAnswer(
        answer -> answer.getArgument(0));

    service = new DefaultFoundItemCommandService(repository, imageRepository, quizService,
        eventPublisher);
  }

  @DisplayName("타입이 카드나 신분증이면, 이미지를 함께 등록할 수 없다.")
  @Test
  void When_TypeIsCardOrId_Then_NotSaveWithImage() {
    // given
    var image = generateImage();
    var type = FoundItemType.CREDIT;
    var foundAt = LocalDateTime.now();

    var command = generateCommand(image, type, foundAt);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("카드나 신분증 사진이 포함되어 있습니다.");
    verify(eventPublisher, never()).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  @DisplayName("습득물을 저장할 때, 이미지를 함께 저장한다.")
  @Test
  void When_SaveFoundItem_Then_SaveWithImage() {
    // given
    given(imageRepository.findFoundImageById(any())).willReturn(Optional.of(generateItem()));
    FoundItemSaveCommand command = generateCommand(generateImage(), FoundItemType.OTHER,
        LocalDateTime.now());

    // when
    service.save(command);

    // then
    verify(repository).save(any(FoundItem.class));
    verify(eventPublisher, times(1)).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  @DisplayName("습득물 저장에 실패하면, 이벤트를 발행하지 않는다.")
  @Test
  void When_FailToSaveFoundItem_Then_DeleteSavedImage() {
    // given
    var command = generateCommand(generateImage(), FoundItemType.OTHER, LocalDateTime.now());
    given(repository.save(any())).willThrow(RuntimeException.class);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).isInstanceOf(RuntimeException.class);
    verify(eventPublisher, never()).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  @DisplayName("타입이 카드나 신분증이 아니면, 이미지는 필수다.")
  @Test
  void When_TypeIsNotCardOrId_Then_RequiredImage() {
    // given
    String image = null;
    var type = FoundItemType.OTHER;
    var foundAt = LocalDateTime.now();

    var command = generateCommand(image, type, foundAt);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("이미지는 필수입니다.");
    verify(eventPublisher, never()).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  @DisplayName("습득물을 저장할 때, 습득 시간은 미래일 수 없다.")
  @Test
  void When_SaveFoundItem_Then_FoundIsInPast() {
    // given
    var image = generateImage();
    var type = FoundItemType.OTHER;
    var foundAt = LocalDateTime.now().plusDays(1);

    var command = generateCommand(image, type, foundAt);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("습득 날짜가 미래입니다.");
    verify(eventPublisher, never()).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  @DisplayName("습득물 저장 시 퀴즈를 생성한다.")
  @Test
  void When_SaveFoundItem_Then_MakeQuiz() {
    // given
    given(imageRepository.findFoundImageById(any())).willReturn(Optional.of(generateItem()));
    FoundItemSaveCommand command = generateCommand(generateImage(), FoundItemType.OTHER,
        LocalDateTime.now());

    // when
    service.save(command);

    // then
    verify(quizService).make(any(QuizMakeCommand.class));
    verify(eventPublisher, times(1)).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  @DisplayName("퀴즈 생성을 실패하면 저장했던 이미지를 삭제한다.")
  @Test
  void When_FailToMakeQuiz_Then_DeleteSavedImage() {
    // given
    FoundItemSaveCommand command = generateCommand(generateImage(), FoundItemType.OTHER,
        LocalDateTime.now());
    doThrow(new RuntimeException()).when(quizService).make(any(QuizMakeCommand.class));

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).isInstanceOf(RuntimeException.class);
    verify(eventPublisher, never()).publishAtLeastOnce(any(FoundItemSaveEvent.class));
  }

  private FoundItemSaveCommand generateCommand(String image, FoundItemType type,
      LocalDateTime foundAt) {
    return new FoundItemSaveCommand(1, image, mock(), type, "저장묘사", "물건묘사", "주소없음", foundAt);
  }

  private String generateImage() {
    return String.format("%s.%s", UUID.randomUUID(), "png");
  }

  private FoundImage generateItem() {
    return new FoundImage(UUID.randomUUID(), "png", mock());
  }
}