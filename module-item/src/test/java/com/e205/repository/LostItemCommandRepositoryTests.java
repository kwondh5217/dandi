package com.e205.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.entity.LostItem;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LostItemCommandRepositoryTests {

  @Autowired
  LostItemCommandRepository lostItemCommandRepository;

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

    lostItemCommandRepository.saveAll(lostItems);

    // when
    Optional<LostItem> result = lostItemCommandRepository.findFirstByMemberIdOrderByCreatedAtDesc(1);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(lostItems.get(9));
  }

  private LostItem generateLostItem(Integer memberId) {
    return LostItem.builder().memberId(memberId).startRouteId(1).endRouteId(2)
        .situationDescription("상황설명").itemDescription("물건설명").build();
  }
}