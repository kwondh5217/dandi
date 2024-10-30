package com.e205.repository;

import com.e205.entity.FoundItem;
import org.springframework.data.repository.CrudRepository;

public interface FoundItemCommandRepository extends CrudRepository<FoundItem, Integer> {

}
