package com.e205.repository;

import com.e205.entity.LostItemAuth;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

public interface LostItemAuthRepository extends CrudRepository<LostItemAuth, Integer> {
  
  @EntityGraph(attributePaths = "lostItem")
  Optional<LostItemAuth> findLostItemAuthByMemberIdAndLostItemId(Integer memberId, Integer lostItemId);
}
