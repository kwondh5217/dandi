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
}
