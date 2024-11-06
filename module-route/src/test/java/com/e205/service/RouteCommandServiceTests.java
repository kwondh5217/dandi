package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.event.RouteSavedEvent;
import com.e205.events.EventPublisher;
import com.e205.exception.RouteError;
import com.e205.exception.RouteException;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.repository.RouteRepository;
import com.e205.service.validator.RouteValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RouteCommandServiceTests {

  private static final Integer MEMBER_ID = 1;
  private static final Integer BAG_ID = 1;
  private static final Integer INVALID_ROUTE_ID = 0;
  private static final Integer VALID_ROUTE_ID = 1;

  @InjectMocks
  private DirectRouteCommandService commandService;

  @Mock
  private RouteRepository routeRepository;

  @Mock
  private BagItemQueryService bagItemQueryService;

  @Mock
  private RouteValidator validator;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  List<SnapshotItem> basedBagItems;
  Snapshot snapshot;

  @BeforeEach
  void setUp() {
    // 초기 가방 스냅샷 아이템
    basedBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, false),
        new SnapshotItem("반지", "💍", 1, false),
        new SnapshotItem("파우치", "👜", 1, false),
        new SnapshotItem("카드", "💳", 1, false)
    );
    snapshot = new Snapshot(BAG_ID, basedBagItems);
  }

  @Test
  @DisplayName("이동 시작 성공 테스트")
  void 이동_시작_성공_테스트() {
    // given
    RouteCreateCommand command = new RouteCreateCommand(MEMBER_ID, BAG_ID);
    Route savedRoute = getRoute();
    given(routeRepository.save(any(Route.class))).willReturn(savedRoute);

    // when
    commandService.createRoute(command);

    // then
    verify(routeRepository).save(any(Route.class));
    ArgumentCaptor<RouteSavedEvent> eventCaptor = ArgumentCaptor.forClass(RouteSavedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());
    RouteSavedEvent publishedEvent = eventCaptor.getValue();
    assertThat(MEMBER_ID).isEqualTo(publishedEvent.memberId());
    assertThat(publishedEvent.payload()).isNotNull();
  }

  private @NotNull Route getRoute() {
    return new Route(VALID_ROUTE_ID, MEMBER_ID, null, 'Y', Snapshot.toJson(snapshot),
        LocalDateTime.now(), LocalDateTime.now());
  }

  @Test
  @DisplayName("존재하지 않는 이동 실패 테스트")
  void 존재하지_않는_이동_실패_테스트() {
    // given
    SnapshotUpdateCommand command = new SnapshotUpdateCommand(INVALID_ROUTE_ID, snapshot);
    given(routeRepository.findById(any(Integer.class))).willReturn(Optional.empty());

    // when
    ThrowingCallable expectThrow = () -> commandService.updateSnapshot(command);

    // then
    assertThatThrownBy(expectThrow).isInstanceOf(RouteException.class);
    verify(routeRepository, never()).save(any(Route.class));
  }

  @Test
  @DisplayName("이미 종료 된 이동 실패 테스트")
  void 이미_종료_된_이동_실패_테스트() {
    // given
    Route route = new Route();
    RouteEndCommand command = new RouteEndCommand(VALID_ROUTE_ID, null);
    given(routeRepository.findById(VALID_ROUTE_ID)).willReturn(Optional.of(route));
    doThrow(new RouteException(RouteError.ENDED_ROUTE)).when(validator).validateEndedRoute(route);

    // when
    ThrowingCallable expectThrow = () -> commandService.endRoute(command);

    // then
    assertThatThrownBy(expectThrow).isInstanceOf(RouteException.class);
    verify(routeRepository, never()).save(any(Route.class));
  }
}
