package com.e205.repository;

import com.e205.entity.FoundItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoundItemQueryRepository extends JpaRepository<FoundItem, Integer> {

  @Query(value = "select f "
      + "from FoundItem f "
      + "where f.memberId = :memberId "
      + "and f.endedAt is null")
  List<FoundItem> findAllByMemberId(@Param("memberId") Integer memberId);
}
