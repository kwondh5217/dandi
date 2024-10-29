package com.e205.interaction.queries;

import com.e205.dto.SnapshotItem;
import java.util.List;

public interface BagItemQueryService {

  List<SnapshotItem> bagItemsOfMember(BagItemsOfMemberQuery query);
}
