package com.e205.domain.item.repository;

import com.e205.domain.item.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends CrudRepository<Item, Integer> {

  @Query("SELECT COALESCE(MAX(i.itemOrder), 0) FROM Item i WHERE i.memberId = :memberId")
  byte findMaxItemOrderByMemberId(@Param("memberId") Integer memberId);

  List<Item> findAllByMemberId(Integer memberId);

  List<Item> findAllById(Iterable<Integer> itemIds);

  boolean existsByNameAndMemberId(String name, Integer memberId);

  boolean existsByNameAndMemberIdAndIdNot(String name, Integer memberId, Integer id);

  @Query("SELECT i FROM Item i WHERE i.memberId = :memberId AND i.id NOT IN " +
      "(SELECT bi.itemId FROM BagItem bi WHERE bi.bagId = :bagId)")
  List<Item> findItemsNotInBag(@Param("memberId") Integer memberId, @Param("bagId") Integer bagId);
}
