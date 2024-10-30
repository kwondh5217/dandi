package com.e205.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.e205.FoundItemType;
import com.e205.command.FoundItemSaveCommand;
import com.e205.entity.FoundItem;
import com.e205.repository.FoundItemCommandRepository;
import com.e205.repository.ItemImageRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

class FoundItemCommandServiceTest {

  FoundItemCommandService service;
  FoundItemCommandRepository repository;
  ItemImageRepository imageRepository;
  ImageService imageService;

  @BeforeEach
  void setUp() {
    repository = mock(FoundItemCommandRepository.class);
    imageRepository = mock(ItemImageRepository.class);
    imageService = mock(ImageService.class);
    given(imageService.save(any())).willReturn(UUID.randomUUID().toString() + ".png");

    service = new DefaultFoundItemCommandService(repository, imageRepository, imageService);
  }

  @DisplayName("타입이 카드나 신분증이면, 이미지를 함께 등록할 수 없다.")
  @Test
  void When_TypeIsCardOrId_Then_NotSaveWithImage() {
    // given
    var image = generateResource();
    var type = FoundItemType.CREDIT;
    var foundAt = LocalDateTime.now();

    var command = generateCommand(image, type, foundAt);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("카드나 신분증 사진이 포함되어 있습니다.");
  }

  @DisplayName("습득물을 저장할 때, 이미지를 함께 저장한다.")
  @Test
  void When_SaveFoundItem_Then_SaveWithImage() {
    // given
    FoundItemSaveCommand command = generateCommand(generateResource(),
        FoundItemType.OTHER, LocalDateTime.now());

    // when
    service.save(command);

    // then
    verify(repository).save(any(FoundItem.class));
    verify(imageService).save(any(Resource.class));
  }

  @DisplayName("습득물 저장에 실패하면, 저장된 이미지를 삭제한다.")
  @Test
  void When_FailToSaveFoundItem_Then_DeleteSavedImage() {
    // given
    var command = generateCommand(generateResource(), FoundItemType.OTHER,
        LocalDateTime.now());
    given(repository.save(any())).willThrow(RuntimeException.class);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).isInstanceOf(RuntimeException.class);
    verify(imageService).delete(any(String.class));
  }

  @DisplayName("타입이 카드나 신분증이 아니면, 이미지는 필수다.")
  @Test
  void When_TypeIsNotCardOrId_Then_RequiredImage() {
    // given
    Resource image = null;
    var type = FoundItemType.OTHER;
    var foundAt = LocalDateTime.now();

    var command = generateCommand(image, type, foundAt);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("이미지는 필수입니다.");
  }

  @DisplayName("습득물을 저장할 때, 습득 시간은 미래일 수 없다.")
  @Test
  void When_SaveFoundItem_Then_FoundIsInPast() {
    // given
    Resource image = generateResource();
    var type = FoundItemType.OTHER;
    var foundAt = LocalDateTime.now().plusDays(1);

    var command = generateCommand(image, type, foundAt);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow).hasMessage("습득 날짜가 미래입니다.");
  }

  private FoundItemSaveCommand generateCommand(Resource image, FoundItemType type,
      LocalDateTime foundAt) {
    return new FoundItemSaveCommand(1, image, mock(), type, "저장묘사", "물건묘사", foundAt);
  }

  private Resource generateResource() {
    return mock(Resource.class);
  }
}