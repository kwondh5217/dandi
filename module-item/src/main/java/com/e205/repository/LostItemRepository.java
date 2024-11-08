package com.e205.repository;

import com.e205.entity.LostItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LostItemRepository extends JpaRepository<LostItem, Integer> {

  Optional<LostItem> findFirstByMemberIdOrderByCreatedAtDesc(Integer memberId);

  @Query(value = "select l "
      + "from LostItem l "
      + "where l.memberId = :memberId "
      + "and l.endedAt is null")
  List<LostItem> findAllByMemberId(@Param("memberId") Integer memberId);
}
