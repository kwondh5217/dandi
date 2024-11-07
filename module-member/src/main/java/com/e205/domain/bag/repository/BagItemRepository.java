package com.e205.domain.bag.repository;

import com.e205.domain.bag.entity.BagItem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BagItemRepository extends JpaRepository<BagItem, Integer> {

  List<BagItem> findAllByBagId(Integer bagId);
  // TODO: <홍성우> 한건한건 삭제된다. Batch 한번에해라
  void deleteAllByBagId(Integer id);

  void deleteByBagIdAndItemId(Integer bagId, Integer itemId);

  List<BagItem> findAllByItemId(Integer itemId);
}