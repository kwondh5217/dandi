package com.e205.domain.bag.repository;

import com.e205.domain.bag.entity.BagItem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BagItemRepository extends JpaRepository<BagItem, Integer> {

  List<BagItem> findAllByBagId(Integer bagId);

  void deleteAllByBagId(Integer id);

  void deleteByBagIdAndItemId(Integer integer, Integer integer1);

  List<BagItem> findAllByItemId(Integer itemId);
}