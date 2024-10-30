package com.e205.domain.bag.repository;

import com.e205.domain.bag.entity.Bag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BagRepository extends JpaRepository<Bag, Integer> {

  @Query("SELECT COALESCE(MAX(b.bagOrder), 0) FROM Bag b WHERE b.memberId = :memberId")
  Byte findMaxBagOrderByMemberId(Integer memberId);

  List<Bag> findAllByMemberId(Integer memberId);

  boolean existsByMemberIdAndName(Integer memberId, String name);

  Optional<Bag> findByIdAndMemberId(Integer bagId, Integer memberId);
}