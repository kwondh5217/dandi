package com.e205.domain.bag.repository;

import com.e205.domain.bag.dto.BagItemDataResponse;
import com.e205.domain.bag.entity.BagItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BagItemRepository extends JpaRepository<BagItem, Integer> {

  List<BagItem> findAllByBagId(Integer bagId);

  @Query("SELECT new com.e205.domain.bag.dto.BagItemDataResponse(bi.itemId, bi.itemOrder, i.name, i.emoticon, i.colorKey) " +
      "FROM BagItem bi JOIN Item i ON bi.itemId = i.id " +
      "WHERE bi.bagId = :bagId")
  List<BagItemDataResponse> findAllItemsByBagId(@Param("bagId") Integer bagId);

  void deleteAllByBagId(Integer id);
}