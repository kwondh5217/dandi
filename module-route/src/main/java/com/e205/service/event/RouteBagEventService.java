package com.e205.service.event;

import com.e205.command.SnapshotUpdateCommand;
import com.e205.command.bag.event.BagChangedEvent;
import com.e205.command.bag.event.BagItemAddEvent;
import com.e205.command.bag.event.BagItemChangedEvent;
import com.e205.command.bag.event.BagItemDeleteEvent;
import com.e205.command.item.payload.ItemPayload;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.event.RouteSavedEvent;
import com.e205.events.EventPublisher;
import com.e205.repository.RouteRepository;
import com.e205.service.RouteCommandService;
import com.e205.service.reader.SnapshotHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Service
public class RouteBagEventService {

  private final ApplicationEventPublisher eventPublisher;
  private final RouteCommandService routeCommandService;
  private final RouteRepository routeRepository;
  private final SnapshotHelper snapshotHelper;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBagChanged(BagChangedEvent event) {
    Integer memberId = event.memberId();
    Integer bagId = event.bagId();

    routeRepository.findFirstByMemberIdAndEndedAtIsNull(memberId).ifPresent(route -> {
      Snapshot baseSnapshot = snapshotHelper.loadBaseSnapshot(memberId, bagId);

      SnapshotUpdateCommand command = new SnapshotUpdateCommand(
          memberId, route.getId(), baseSnapshot
      );

      routeCommandService.updateSnapshot(command);

      eventPublisher.publishEvent(new RouteSavedEvent(memberId, Snapshot.toJson(baseSnapshot)));
    });
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBagItemAdd(BagItemAddEvent event) {
    ItemPayload item = event.itemPayload();
    Integer memberId = item.memberId();

    routeRepository.findFirstByMemberIdAndEndedAtIsNull(memberId).ifPresent(route -> {
      Snapshot currentSnapshot = snapshotHelper.loadCurrentSnapshot(route);
      SnapshotItem snapshotItem = setSnapshotItem(item);
      Snapshot updatedSnapshot = currentSnapshot.addItem(snapshotItem);

      SnapshotUpdateCommand comm = new SnapshotUpdateCommand(
          memberId, route.getId(), updatedSnapshot
      );

      routeCommandService.updateSnapshot(comm);

      eventPublisher.publishEvent(new RouteSavedEvent(memberId, Snapshot.toJson(updatedSnapshot)));
    });
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBagItemDelete(BagItemDeleteEvent event) {
    ItemPayload item = event.itemPayload();
    Integer memberId = item.memberId();

    routeRepository.findFirstByMemberIdAndEndedAtIsNull(memberId).ifPresent(route -> {
      Snapshot currentSnapshot = snapshotHelper.loadCurrentSnapshot(route);
      SnapshotItem snapshotItem = setSnapshotItem(item);
      Snapshot updatedSnapshot = currentSnapshot.removeItem(snapshotItem);

      SnapshotUpdateCommand comm = new SnapshotUpdateCommand(
          memberId, route.getId(), updatedSnapshot
      );

      routeCommandService.updateSnapshot(comm);
    });
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBagItemChanged(BagItemChangedEvent event) {
    ItemPayload previousItem = event.previousItemPayload();
    ItemPayload updatedItem = event.itemPayload();
    Integer memberId = updatedItem.memberId();

    routeRepository.findFirstByMemberIdAndEndedAtIsNull(memberId).ifPresent(route -> {
      Snapshot currentSnapshot = snapshotHelper.loadCurrentSnapshot(route);
      Snapshot updatedSnapshot = currentSnapshot
          .removeItem(setSnapshotItem(previousItem))
          .addItem(setSnapshotItem(updatedItem));

      SnapshotUpdateCommand comm = new SnapshotUpdateCommand(
          memberId, route.getId(), updatedSnapshot
      );

      routeCommandService.updateSnapshot(comm);

      eventPublisher.publishEvent(new RouteSavedEvent(memberId, Snapshot.toJson(updatedSnapshot)));
    });
  }

  private static SnapshotItem setSnapshotItem(ItemPayload item) {
    return SnapshotItem.builder()
        .name(item.name())
        .emoticon(item.emoticon())
        .type((int) item.colorKey())
        .isChecked(false)
        .build();
  }
}
