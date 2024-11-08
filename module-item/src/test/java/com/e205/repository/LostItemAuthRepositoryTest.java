package com.e205.repository;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import com.e205.entity.LostItem;
import com.e205.entity.LostItemAuth;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LostItemAuthRepositoryTest {

  @Autowired
  EntityManager entityManager;
  @Autowired
  LostItemAuthRepository lostItemAuthRepository;
  @Autowired
  LostItemRepository lostItemRepository;

  @DisplayName("LostItemAuth를 조회하면 LostItem이 즉시 로딩된다.")
  @Test
  void When_GetLostItemInAuth_Then_GetWithNoQuery() {
    // given

    LostItem lostItem = new LostItem(1, 1, 2, "상황묘사", "물건묘사", now());
    LostItemAuth lostItemAuth = new LostItemAuth(1, lostItem);
    lostItemRepository.save(lostItem);
    lostItemAuthRepository.save(lostItemAuth);
    entityManager.flush();
    entityManager.clear();

    // when
    Statistics statistics = entityManager.unwrap(Session.class).getSessionFactory().getStatistics();
    statistics.setStatisticsEnabled(true);

    Optional<LostItemAuth> result = lostItemAuthRepository.findLostItemAuthByMemberIdAndLostItemId(
        1, lostItem.getId());

    // then
    assertThat(result).isPresent();
    result.get().getLostItem().isEnded();
    assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
  }
}