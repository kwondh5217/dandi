package com.e205.service;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.e205.base.item.command.LostItemGrantCommand;
import com.e205.base.item.command.LostItemSaveCommand;
import com.e205.base.item.service.LostItemCommandService;
import com.e205.entity.LostImage;
import com.e205.entity.LostItem;
import com.e205.repository.ItemImageRepository;
import com.e205.repository.LostItemAuthRepository;
import com.e205.repository.LostItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LostItemCommandServiceTests {

  LostItemCommandService service;
  LostItemRepository repository;
  ItemImageRepository itemImageRepository;
  LostItemAuthRepository lostItemAuthRepository;

  @BeforeEach
  void setUp() {
    repository = mock(LostItemRepository.class);
    itemImageRepository = mock(ItemImageRepository.class);
    lostItemAuthRepository = mock(LostItemAuthRepository.class);
    service = new DefaultLostItemCommandService(repository, itemImageRepository, lostItemAuthRepository);
  }

  @DisplayName("분실물을 저장할 수 있다.")
  @Test
  void saveLostItem() {
    // given
    given(itemImageRepository.findLostImageById(any())).willReturn(Optional.of(generateItem()));
    var images = Stream.generate(this::generateImage).limit(3).toList();
    var command = generateSaveCommand(images);

    LostItem mockItem = mock(LostItem.class);
    given(mockItem.getMemberId()).willReturn(1);
    given(mockItem.getId()).willReturn(1);
    given(repository.save(any(LostItem.class))).willReturn(mockItem);
    given(repository.exists(any())).willReturn(true);

    // when
    service.save(command);

    // then
    verify(repository).save(any(LostItem.class));
  }

  @DisplayName("이미지가 3개 초과일 경우 분실물 저장에 실패한다.")
  @Test
  void When_ImageCountGreaterThan3_Then_FailToSaveLostItem() {
    // given
    var images = Stream.generate(this::generateImage).limit(5).toList();
    var command = generateSaveCommand(images);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow);
  }

  @DisplayName("제한 시간 이내에 다시 등록하려고 한 경우 분실물 저장에 실패한다.")
  @Test
  void When_SaveAgainInCoolTime_Then_FailToSaveLostItem() {
    // given
    var images = Stream.generate(this::generateImage).limit(3).toList();
    var lastSaved = new LostItem(1, 1, 2, "상황묘사", "물건묘사", now());
    var command = generateSaveCommand(images);
    given(repository.findFirstByMemberIdOrderByCreatedAtDesc(1)).willReturn(Optional.of(lastSaved));

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow);
  }

  @DisplayName("이미지가 없어도 분실물 등록에 성공한다.")
  @Test
  void When_WithoutImage_Then_SuccessToSaveLostItem() {
    // given
    LostItem mockItem = mock(LostItem.class);
    given(mockItem.getMemberId()).willReturn(1);
    given(mockItem.getId()).willReturn(1);
    given(repository.save(any(LostItem.class))).willReturn(mockItem);
    given(repository.exists(any())).willReturn(true);

    LostItemSaveCommand command = generateSaveCommand(List.of());

    // when
    service.save(command);

    // then
    verify(repository).save(any(LostItem.class));
  }

  @DisplayName("분실물 저장에 실패하면 저장한 이미지를 삭제한다.")
  @Test
  void When_FailToSaveLostItem_Then_DeleteSavedImages() {
    // given
    var images = Stream.generate(this::generateImage).limit(3).toList();
    var command = generateSaveCommand(images);

    given(repository.save(any(LostItem.class))).willThrow(RuntimeException.class);

    // when
    ThrowingCallable expectThrow = () -> service.save(command);

    // then
    assertThatThrownBy(expectThrow);
  }

  @DisplayName("권한 부여 시 분실물이 존재하지 않으면 예외가 발생한다.")
  @Test
  void When_GrantNotExistsItem_Then_ThrowException() {
    // given
    given(repository.exists(any())).willReturn(false);

    LostItemGrantCommand command = new LostItemGrantCommand(1, 1);

    // when
    ThrowingCallable expectThrow = () -> service.grant(command);

    // then
    assertThatThrownBy(expectThrow).cause().hasMessage("분실물이 존재하지 않습니다.");
  }

  @DisplayName("분실물 권한이 이미 존재하면, 권한을 생성하지 않는다.")
  @Test
  void When_AlreadyExistsAuth_Then_NotCreateAuth() {
    // given
    given(repository.exists(any())).willReturn(true);
    given(lostItemAuthRepository.existsByMemberIdAndLostItemId(1, 1)).willReturn(true);

    LostItemGrantCommand command = new LostItemGrantCommand(1, 1);

    // when
    service.grant(command);

    // then
    verify(lostItemAuthRepository, never()).save(any());
  }

  private static LostItemSaveCommand generateSaveCommand(List<String> images) {
    return new LostItemSaveCommand(1, images, 1, 2, "상황 묘사", "물건 묘사", now());
  }

  private String generateImage() {
    return String.format("%s.%s", UUID.randomUUID(), "png");
  }

  private LostImage generateItem() {
    return new LostImage(UUID.randomUUID(), "png", mock());
  }
}