package com.e205.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e205.dto.LostItemSaveCommand;
import com.e205.entity.LostItem;
import com.e205.events.EventPublisher;
import com.e205.message.LostItemSaveEvent;
import com.e205.repository.LostItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

@ExtendWith(MockitoExtension.class)
class LostItemServiceTests {

  LostItemService service;
  EventPublisher eventPublisher;
  LostItemRepository repository;

  @BeforeEach
  void setUp() {
    eventPublisher = mock(EventPublisher.class);
    repository = mock(LostItemRepository.class);
    service = new DefaultLostItemService(repository, eventPublisher);
  }

  @DisplayName("분실물을 저장할 수 있다.")
  @Test
  void saveLostItem() {
    // given
    List<Resource> images = Stream.generate(this::generateImages).limit(3)
        .toList();
    LostItemSaveCommand command = new LostItemSaveCommand(1, images, mock(), "상황 묘사", "물건 묘사");

    // when
    service.save(command);

    // then
    verify(repository).save(any(LostItem.class));
  }

  @DisplayName("이미지가 3개 초과일 경우 분실물 저장에 실패한다.")
  @Test
  void When_ImageCountGreaterThan3_Then_FailToSaveLostItem() {
    // given
    List<Resource> images = Stream.generate(this::generateImages).limit(5)
        .toList();
    LostItemSaveCommand command = new LostItemSaveCommand(1, images, mock(), "상황 묘사", "물건 묘사");

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow);
  }

  @DisplayName("제한 시간 이내에 다시 등록하려고 한 경우 분실물 저장에 실패한다.")
  @Test
  void When_SaveAgainInCoolTime_Then_FailToSaveLostItem() {
    // given
    List<Resource> images = Stream.generate(this::generateImages).limit(3)
        .toList();
    LostItem lastSaved = new LostItem(1, mock(), "상황묘사", "물건묘사");
    LostItemSaveCommand command = new LostItemSaveCommand(1, images, mock(), "상황묘사", "물건묘사");
    when(repository.findFirstByMemberIdOrderByCreatedAtDesc(1)).thenReturn(Optional.of(lastSaved));

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow);
  }

  @DisplayName("분실물 등록에 성공하면 이벤트가 발행된다.")
  @Test
  void When_SaveSuccess_Then_PublishEvent() {
    // given
    List<Resource> images = Stream.generate(this::generateImages).limit(3)
        .toList();
    LostItemSaveCommand command = new LostItemSaveCommand(1, images, mock(), "상황 묘사", "물건 묘사");

    // when
    service.save(command);

    // then
    verify(eventPublisher).publish(any(LostItemSaveEvent.class));
  }

  @DisplayName("분실물 등록에 실패하면 이벤트가 발행되지 않는다.")
  @Test
  void When_SaveFail_Then_NotPublishEvent() {
    // given
    List<Resource> images = Stream.generate(this::generateImages).limit(5)
        .toList();
    LostItemSaveCommand command = new LostItemSaveCommand(1, images, mock(), "상황 묘사", "물건 묘사");

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow);
    verify(eventPublisher, times(0)).publish(any(LostItemSaveEvent.class));
  }

  private Resource generateImages() {
    return mock(Resource.class);
  }
}