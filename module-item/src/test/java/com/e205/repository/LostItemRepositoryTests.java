package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

import com.e205.entity.LostItem;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LostItemRepositoryTests {

  @Autowired
  LostItemRepository lostItemRepository;

  @DisplayName("사용자의 SOS 요청 중 최신을 가져오는 쿼리 동작 확인")
  @Test
  void Verify_LastSavedLostItemQuery() {
    // given
    List<LostItem> lostItems = Stream.generate(() -> {
      try {
        Thread.sleep(10);
        return generateLostItem(1);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }).limit(10).toList();

    lostItemRepository.saveAll(lostItems);

    // when
    Optional<LostItem> result = lostItemRepository.findFirstByMemberIdOrderByCreatedAtDesc(1);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(lostItems.get(9));
  }

  @DisplayName("사용자 분실물 중 종료되지 않은 것만 조회")
  @Test
  void Verify_MembersLostItemQuery() {
    // given
    List<LostItem> endedLostItems = Stream.generate(() -> generateLostItem(1))
        .limit(3)
        .peek(LostItem::end)
        .toList();
    List<LostItem> notEndedLostItems = Stream.generate(() -> generateLostItem(1))
        .limit(3)
        .toList();
    lostItemRepository.saveAll(endedLostItems);
    lostItemRepository.saveAll(notEndedLostItems);

    // when
    List<LostItem> result = lostItemRepository.findAllByMemberId(1);

    // then
    assertThat(result).isNotEmpty()
        .size().isEqualTo(3);
    assertThat(result).noneMatch(LostItem::isEnded);
  }

  private LostItem generateLostItem(Integer memberId) {
    return LostItem.builder().memberId(memberId).startRouteId(1).endRouteId(2)
        .situationDescription("상황설명").itemDescription("물건설명").build();
  }
}