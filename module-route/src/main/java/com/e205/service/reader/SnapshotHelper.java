package com.e205.service.reader;

import com.e205.command.bag.payload.BagItemPayload;
import com.e205.command.bag.query.ReadAllBagItemsQuery;
import com.e205.command.bag.query.ReadAllItemInfoQuery;
import com.e205.command.bag.service.BagQueryService;
import com.e205.command.item.payload.ItemPayload;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

  public static String mergeSnapshots(Snapshot baseSnapshot, Snapshot currentSnapshot) {
    Map<String, SnapshotItem> currentItemsMap = Optional.ofNullable(currentSnapshot)
        .map(snapshot -> snapshot.items().stream()
            .collect(Collectors.toMap(SnapshotItem::name, item -> item)))
        .orElse(Collections.emptyMap());

    List<SnapshotItem> mergedItems = baseSnapshot.items().stream()
        .map(baseItem -> {
          SnapshotItem currentItem = currentItemsMap.get(baseItem.name());
          if (currentItem != null) {
            return currentItem;
          }
          return getBaseItem(baseItem);
        })
        .toList();

    return Snapshot.toJson(new Snapshot(baseSnapshot.bagId(), mergedItems));
  }

  private static SnapshotItem getBaseItem(SnapshotItem baseItem) {
    return SnapshotItem.builder()
        .name(baseItem.name())
        .emoticon(baseItem.emoticon())
        .type(baseItem.type())
        .isChecked(false)
        .build();
  }
}
