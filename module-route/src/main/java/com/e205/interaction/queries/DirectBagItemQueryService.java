package com.e205.interaction.queries;

import com.e205.dto.SnapshotItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DirectBagItemQueryService implements BagItemQueryService {

  @Override
  public List<SnapshotItem> bagItemsOfMember(BagItemsOfMemberQuery query) {
    return List.of();
  }
}
