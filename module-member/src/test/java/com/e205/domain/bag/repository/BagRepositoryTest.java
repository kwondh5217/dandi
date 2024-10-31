package com.e205.domain.bag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.domain.bag.entity.Bag;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BagRepositoryTest {

  @Autowired
  private BagRepository bagRepository;

  @DisplayName("사용자의 가방 중 최대 bagOrder를 가져오는 쿼리 동작 확인")
  @Test
  void findMaxBagOrderByMemberId_ShouldReturnMaxOrder() {
    // Given
    Integer memberId = 1;
    bagRepository.save(
        Bag.builder().name("bag1").memberId(memberId).bagOrder((byte) 1).enabled('Y').build());
    bagRepository.save(
        Bag.builder().name("bag2").memberId(memberId).bagOrder((byte) 3).enabled('Y').build());
    bagRepository.save(
        Bag.builder().name("bag3").memberId(memberId).bagOrder((byte) 2).enabled('Y').build());

    // When
    Byte maxOrder = bagRepository.findMaxBagOrderByMemberId(memberId);

    // Then
    assertThat(maxOrder).isEqualTo((byte) 3);
  }

  @DisplayName("사용자의 모든 가방을 가져오는 쿼리 동작 확인")
  @Test
  void findAllByMemberId_ShouldReturnAllBagsForMember() {
    // Given
    Integer memberId = 1;
    bagRepository.save(
        Bag.builder().name("bag1").memberId(memberId).bagOrder((byte) 1).enabled('Y').build());
    bagRepository.save(
        Bag.builder().name("bag1").memberId(memberId).bagOrder((byte) 2).enabled('Y').build());
    bagRepository.save(
        Bag.builder().name("bag1").memberId(2).bagOrder((byte) 1).enabled('Y').build());

    // When
    List<Bag> bags = bagRepository.findAllByMemberId(memberId);

    // Then
    assertThat(bags).hasSize(2);
    assertThat(bags).extracting("memberId").containsOnly(memberId);
  }
}