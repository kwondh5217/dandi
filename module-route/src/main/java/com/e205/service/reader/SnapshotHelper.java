package com.e205.service.reader;

import com.e205.command.RouteCreateCommand;
import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.query.ReadAllBagItemsQuery;
import com.e205.command.bag.query.ReadAllItemInfoQuery;
import com.e205.command.bag.service.BagQueryService;
import com.e205.command.item.payload.ItemPayload;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SnapshotHelper {

  private final BagQueryService bagQueryService;

  public Snapshot loadBaseSnapshot(Integer memberId, Integer bagId) {
    ReadAllBagItemsQuery query = new ReadAllBagItemsQuery(memberId, bagId);
    List<Integer> itemIds = bagQueryService.readAllBagItemsByBagId(query)
        .stream()
        .map(BagItemPayload::itemId)
        .toList();

    ReadAllItemInfoQuery itemInfoQuery = new ReadAllItemInfoQuery(itemIds);
    List<ItemPayload> itemPayloads = bagQueryService.readAllByItemIds(itemInfoQuery);
    List<SnapshotItem> snapshotItems = itemPayloads.stream()
        .map(item -> SnapshotItem.builder()
            .name(item.name())
            .emoticon(item.emoticon())
            .type((int) item.colorKey())
            .isChecked(false)
            .build()
        )
        .toList();

    return new Snapshot(bagId, snapshotItems);
  }

  public Snapshot loadCurrentSnapshot(Route route) {
    return Snapshot.fromJson(route.getSnapshot());
  }
}
