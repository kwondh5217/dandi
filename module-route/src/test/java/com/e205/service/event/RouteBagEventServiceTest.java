package com.e205.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e205.command.SnapshotUpdateCommand;
import com.e205.command.bag.event.BagChangedEvent;
import com.e205.command.bag.event.BagItemAddEvent;
import com.e205.command.bag.event.BagItemChangedEvent;
import com.e205.command.bag.event.BagItemDeleteEvent;
import com.e205.command.item.payload.ItemPayload;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.event.RouteSavedEvent;
import com.e205.events.EventPublisher;
import com.e205.repository.RouteRepository;
import com.e205.service.RouteCommandService;
import com.e205.service.reader.SnapshotHelper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouteBagEventServiceTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private RouteCommandService routeCommandService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private SnapshotHelper snapshotHelper;

    @InjectMocks
    private RouteBagEventService routeBagEventService;

    @Test
    @DisplayName("BagChanged 이벤트 처리 테스트")
    void BagChanged_이벤트_처리_테스트() {
        // given
        BagChangedEvent event = new BagChangedEvent(1, 1);
        Route mockRoute = mock(Route.class);
        when(mockRoute.getId()).thenReturn(1);
        Snapshot mockSnapshot = createInitialSnapshot("😊");
        when(routeRepository.findFirstByMemberIdAndEndedAtIsNull(1)).thenReturn(Optional.of(mockRoute));
        when(snapshotHelper.loadBaseSnapshot(1, 1)).thenReturn(mockSnapshot);

        // when
        routeBagEventService.handleBagChanged(event);

        // then
        verify(snapshotHelper, times(1)).loadBaseSnapshot(1, 1);

        // updateSnapshot 메서드 호출 검증
        ArgumentCaptor<SnapshotUpdateCommand> commandCaptor = ArgumentCaptor.forClass(
                SnapshotUpdateCommand.class);
        verify(routeCommandService, times(1)).updateSnapshot(commandCaptor.capture());

        // Snapshot 업데이트 검증
        assertThat(commandCaptor.getValue().snapshot()).isEqualTo(mockSnapshot);

        // Route ID 검증
        assertThat(commandCaptor.getValue().routeId()).isEqualTo(mockRoute.getId());

        // 이벤트 발행 검증
        verify(eventPublisher, times(1)).publishAtLeastOnce(any(RouteSavedEvent.class));
    }

    @Test
    @DisplayName("BagItemAdd 이벤트 처리 테스트")
    void BagItemAdd_이벤트_처리_테스트() {
        // given
        ItemPayload itemPayload = new ItemPayload(1, 1, "😊", "name", (byte) 1, (byte) 1);
        BagItemAddEvent event = new BagItemAddEvent(itemPayload);
        Snapshot mockSnapshot = createInitialSnapshot("🙂");
        SnapshotItem newItem = createSnapshotItem("name", "😊", 1);
        Snapshot updatedSnapshot = mockSnapshot.addItem(newItem);
        mockRouteAndSnapshot(mockSnapshot);

        // when
        routeBagEventService.handleBagItemAdd(event);

        // then
        // 최근 스냅샷을 불러왔는지 검증
        verify(snapshotHelper, times(1)).loadCurrentSnapshot(any(Route.class));

        // updateSnapshot 메서드 호출 검증
        ArgumentCaptor<SnapshotUpdateCommand> captor = ArgumentCaptor.forClass(
                SnapshotUpdateCommand.class);
        verify(routeCommandService, times(1)).updateSnapshot(captor.capture());

        // Snapshot이 올바르게 업데이트되었는지 확인
        assertThat(captor.getValue().snapshot()).isEqualTo(updatedSnapshot);

        // 이벤트 발행 검증
        verify(eventPublisher, times(1)).publishAtLeastOnce(any(RouteSavedEvent.class));
    }

    @Test
    @DisplayName("BagItemDelete 이벤트 처리 테스트")
    void BagItemDelete_이벤트_처리_테스트() {
        // given
        ItemPayload itemPayload = new ItemPayload(1, 1, "😊", "name", (byte) 1, (byte) 1);
        BagItemDeleteEvent event = new BagItemDeleteEvent(itemPayload);
        SnapshotItem initialItem = createSnapshotItem("name", "😊", 1);
        Snapshot mockSnapshot = new Snapshot(1, List.of(initialItem));
        Snapshot updatedSnapshot = mockSnapshot.removeItem(initialItem);
        mockRouteAndSnapshot(mockSnapshot);

        // when
        routeBagEventService.handleBagItemDelete(event);

        // then
        // 최근 스냅샷을 불러왔는지 검증
        verify(snapshotHelper, times(1)).loadCurrentSnapshot(any(Route.class));

        // updateSnapshot 메서드 호출 검증
        ArgumentCaptor<SnapshotUpdateCommand> captor = ArgumentCaptor.forClass(
                SnapshotUpdateCommand.class);
        verify(routeCommandService, times(1)).updateSnapshot(captor.capture());

        // 아이템 삭제 후 Snapshot이 올바르게 업데이트되었는지 확인
        assertThat(captor.getValue().snapshot()).isEqualTo(updatedSnapshot);
    }

    @Test
    @DisplayName("BagItemChanged 이벤트 처리 테스트")
    void BagItemChanged_이벤트_처리_테스트() {
        // given
        ItemPayload previousItem = new ItemPayload(1, 1, "😊", "pre", (byte) 1, (byte) 1);
        ItemPayload updatedItem = new ItemPayload(2, 1, "🙂", "update", (byte) 2, (byte) 1);
        BagItemChangedEvent event = new BagItemChangedEvent(previousItem, updatedItem);
        SnapshotItem initialItem = createSnapshotItem("pre", "😊", 1);
        Snapshot mockSnapshot = new Snapshot(1, List.of(initialItem));
        SnapshotItem newItem = createSnapshotItem("update", "🙂", 2);
        Snapshot intermediateSnapshot = mockSnapshot.removeItem(initialItem);
        Snapshot updatedSnapshot = intermediateSnapshot.addItem(newItem);

        mockRouteAndSnapshot(mockSnapshot);

        // when
        routeBagEventService.handleBagItemChanged(event);

        // then
        // 최근 스냅샷을 불러왔는지 검증
        verify(snapshotHelper, times(1)).loadCurrentSnapshot(any(Route.class));

        // updateSnapshot 메서드 호출 검증
        ArgumentCaptor<SnapshotUpdateCommand> captor = ArgumentCaptor.forClass(
                SnapshotUpdateCommand.class);
        verify(routeCommandService, times(1)).updateSnapshot(captor.capture());

        // 이벤트 발행 검증
        verify(eventPublisher, times(1)).publishAtLeastOnce(any(RouteSavedEvent.class));

        // 아이템 변경(삭제, 추가) 후 Snapshot이 올바르게 업데이트되었는지 확인
        assertThat(captor.getValue().snapshot()).isEqualTo(updatedSnapshot);
    }

    @Test
    @DisplayName("진행중인 이동이 없는 경우 스냅샷 업데이트 하지 않는 테스트")
    void 진행중인_이동이_없는_경우_스냅샷_업데이트_하지_않는_테스트() {
        // given
        ItemPayload previousItem = new ItemPayload(1, 1, "😊", "pre", (byte) 1, (byte) 1);
        ItemPayload updatedItem = new ItemPayload(2, 1, "🙂", "update", (byte) 2, (byte) 1);
        BagItemChangedEvent event = new BagItemChangedEvent(previousItem, updatedItem);
        given(routeRepository.findFirstByMemberIdAndEndedAtIsNull(1)).willReturn(Optional.empty());

        // when
        routeBagEventService.handleBagItemChanged(event);

        // then
        // 최근 스냅샷을 불러왔는지 검증
        verify(routeRepository, times(1)).findFirstByMemberIdAndEndedAtIsNull(any());
        // updateSnapshot 메서드 호출 X 검증
        ArgumentCaptor<SnapshotUpdateCommand> captor = ArgumentCaptor.forClass(SnapshotUpdateCommand.class);
        verify(routeCommandService, never()).updateSnapshot(captor.capture());

        // 이벤트 발행 X 검증
        verify(eventPublisher, never()).publishAtLeastOnce(any(RouteSavedEvent.class));

    }

    private SnapshotItem createSnapshotItem(String name, String emoticon, int type) {
        return SnapshotItem.builder()
                .name(name)
                .emoticon(emoticon)
                .type(type)
                .isChecked(false)
                .build();
    }

    private Snapshot createInitialSnapshot(String emoticon) {
        SnapshotItem item = createSnapshotItem("item1", emoticon, 1);
        return new Snapshot(1, List.of(item));
    }

    private void mockRouteAndSnapshot(Snapshot snapshot) {
        Route mockRoute = mock(Route.class);
        when(routeRepository.findFirstByMemberIdAndEndedAtIsNull(1)).thenReturn(
                Optional.of(mockRoute));
        when(snapshotHelper.loadCurrentSnapshot(mockRoute)).thenReturn(snapshot);
    }
}
