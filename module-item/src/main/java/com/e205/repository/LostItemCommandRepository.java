package com.e205.repository;

import com.e205.entity.LostItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemCommandRepository extends JpaRepository<LostItem, Integer> {

  Optional<LostItem> findFirstByMemberIdOrderByCreatedAtDesc(Integer memberId);
}
