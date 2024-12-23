package com.e205.repository;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import com.e205.base.item.FoundItemType;
import com.e205.entity.FoundImage;
import com.e205.entity.FoundItem;
import com.e205.entity.LostImage;
import com.e205.entity.LostItem;
import java.io.Closeable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(DefaultItemImageRepository.class)
class DefaultItemImageRepositoryTest {

  @Autowired
  ItemImageRepository repository;
  @Autowired
  LostItemRepository lostItemRepository;
  @Autowired
  FoundItemCommandRepository foundItemCommandRepository;

  @DisplayName("분실물 아이디로 이미지를 조회할 수 있다.")
  @Test
  void When_FindWithLostItemId_Then_ReturnAllImageForLostItem() {
    // given
    LostItem lostItem = new LostItem(1, 1, 2, "상황묘사", "물건묘사", now());
    lostItemRepository.save(lostItem);

    List<LostImage> images = Stream.generate(UUID::randomUUID).limit(3)
        .map(uuid -> new LostImage(uuid, "png", lostItem))
        .map(repository::save).toList();

    // when
    List<LostImage> result = repository.findAllByLostItemId(lostItem.getId());

    // then
    assertThat(result).isNotEmpty().allMatch(images::contains);
  }

  @DisplayName("최근 생성된 습득물 이미지 20개를 조회할 수 있다.")
  @Test
  void When_FindFoundItemsByLatestOrderWithLimit20_Then_ReturnSuccess() {
    // given
    FoundItem foundItem = new FoundItem(1, 1D, 1D, "묘사", "저장", FoundItemType.OTHER,
        now(), "주소없음");
    foundItemCommandRepository.save(foundItem);

    List<FoundImage> foundImages;
    try (var mockTime = new MockLocalDateTime()) {
      foundImages = Stream.generate(() -> generateFoundImage(foundItem)).limit(30)
          .map(repository::save).toList();
    }
    List<FoundImage> expectTop20 = foundImages.stream().skip(10).toList();

    // when
    List<FoundImage> top20 = repository.findTopFoundImagesByCreateAtDesc(
        20);

    // then
    assertThat(top20).hasSize(20).allMatch(expectTop20::contains);
  }

  private FoundImage generateFoundImage(FoundItem foundItem) {
    return new FoundImage(UUID.randomUUID(), "png", foundItem);
  }

  private static class MockLocalDateTime implements Closeable {

    private final LocalDateTime fixedTime = now();
    private final MockedStatic<LocalDateTime> mockTime = mockStatic(LocalDateTime.class);
    private int callCount = 0;

    public MockLocalDateTime() {
      mockTime.when(LocalDateTime::now).thenAnswer(answer -> {
        callCount++;
        return fixedTime.plus(callCount * 10L, ChronoUnit.MILLIS);
      });
    }

    @Override
    public void close() {
      mockTime.close();
    }
  }
}