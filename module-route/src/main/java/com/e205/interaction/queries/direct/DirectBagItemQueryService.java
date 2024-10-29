package com.e205.interaction.queries.direct;

import com.e205.dto.SnapshotItem;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.query.BagItemsOfMemberQuery;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DirectBagItemQueryService implements BagItemQueryService {

  @Override
  public List<SnapshotItem> bagItemsOfMember(BagItemsOfMemberQuery query) {
    return List.of();
  }
}
