package com.e205.repository;

import com.e205.entity.FoundItem;
import java.util.List;
import java.util.Optional;

public interface FoundItemQueryRepository {

  List<FoundItem> findAllByMemberId(Integer memberId);

  Optional<FoundItem> findById(Integer foundId);

  List<FoundItem> findReadable(Integer memberId);
}
