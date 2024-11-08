package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.FoundItemType;
import com.e205.entity.FoundItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class FoundItemQueryRepositoryTest {

  @Autowired
  private FoundItemCommandRepository foundItemCommandRepository;
  @Autowired
  private FoundItemQueryRepository foundItemQueryRepository;

  @DisplayName("사용자 습득물 중 종료되지 않은 것만 조회")
  @Test
  void Verify_MembersFoundItemQuery() {
    // given
    List<FoundItem> endedFoundItem = Stream.generate(() -> generateFoundItem(1))
        .limit(3)
        .peek(FoundItem::end)
        .toList();
    List<FoundItem> notEndedFoundItem = Stream.generate(() -> generateFoundItem(1))
        .limit(3)
        .toList();
    foundItemCommandRepository.saveAll(endedFoundItem);
    foundItemCommandRepository.saveAll(notEndedFoundItem);

    // when
    List<FoundItem> result = foundItemQueryRepository.findAllByMemberId(1);

    // then
    assertThat(result).isNotEmpty()
        .size().isEqualTo(3);
    assertThat(result).noneMatch(FoundItem::isEnded);
  }

  private FoundItem generateFoundItem(Integer memberId) {
    return FoundItem.builder()
        .foundAt(LocalDateTime.now())
        .lat(1D)
        .lon(1D)
        .type(FoundItemType.OTHER)
        .description("묘사")
        .savePlace("저장")
        .memberId(memberId)
        .build();
  }
}