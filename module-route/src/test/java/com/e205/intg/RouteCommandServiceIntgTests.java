package com.e205.intg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.e205.TestConfiguration;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.interaction.commands.RouteCreateCommand;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.repository.RouteRepository;
import com.e205.service.RouteCommandService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

//@Sql("/test-sql/route.sql")
//@ActiveProfiles(value = "test")
//@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest(classes = TestConfiguration.class)
public class RouteCommandServiceIntgTests {

  @Autowired
  private RouteCommandService routeCommandService;

  @Autowired
  private RouteRepository routeRepository;

  @MockBean
  private BagItemQueryService bagItemQueryService;

  private Integer memberId;
  private RouteCreateCommand requestBagId1;
  private RouteCreateCommand requestBagId2;

  List<SnapshotItem> basedBagItems;
  List<SnapshotItem> currentBagItems;
  List<SnapshotItem> newBagItems;
  Snapshot initSnapshot;

  @BeforeEach
  void setUp() {
    memberId = 1;
    requestBagId1 = new RouteCreateCommand(1, LocalDateTime.now());
    requestBagId2 = new RouteCreateCommand(2, LocalDateTime.now());

    // 초기 가방 스냅샷 아이템
    basedBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, false),
        new SnapshotItem("반지", "💍", 1, false),
        new SnapshotItem("파우치", "👜", 1, false),
        new SnapshotItem("카드", "💳", 1, false)
    );

    // 1번 가방 최근 스냅샷 아이템
    currentBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, true),
        new SnapshotItem("반지", "💍", 1, true),
        new SnapshotItem("파우치", "👜", 1, true),
        new SnapshotItem("카드", "💳", 1, true)
    );

    // 2번 가방 (가방 ID가 일치하지 않는 경우 반환할 스냅샷 아이템)
    newBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, false),
        new SnapshotItem("반지", "💍", 1, false)
    );

    initSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
  }

  @Test
  @DisplayName("생성된_이동이_이전_스냅샷을_포함할_때_요청과_같은_bagId_테스트")
  @Transactional
  void 생성된_이동이_이전_스냅샷을_포함할_때_요청과_같은_bagId_테스트() {
    // given
    routeRepository.save(
        Route.toEntity(memberId, requestBagId1, Snapshot.toJson(initSnapshot)));
    Route previousRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).get();
    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(currentBagItems);

    // when
    routeCommandService.createRoute(requestBagId1, memberId);
    Route currentRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).get();

    // then
    Snapshot previousSnapshot = Snapshot.fromJson(previousRoute.getSnapshot());
    Snapshot currentSnapshot = Snapshot.fromJson(currentRoute.getSnapshot());

    assertThat(currentSnapshot.bagId()).isEqualTo(previousSnapshot.bagId());
    assertThat(currentSnapshot.items()).isEqualTo(previousSnapshot.items());
  }

  @Test
  @DisplayName("생성된_이동이_이전_스냅샷을_포함할_때_요청과_다른_bagId_테스트")
  @Transactional
  void 생성된_이동이_기본_스냅샷을_포함할_때_요청과_다른_bagId_테스트() {
    // given
    routeRepository.save(Route.toEntity(memberId, requestBagId1, Snapshot.toJson(initSnapshot)));
    Route previousRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).get();
    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(newBagItems);

    // when
    routeCommandService.createRoute(requestBagId2, memberId);
    Route currentRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).get();

    // then
    Snapshot previousSnapshot = Snapshot.fromJson(previousRoute.getSnapshot());
    Snapshot currentSnapshot = Snapshot.fromJson(currentRoute.getSnapshot());

    assertThat(currentSnapshot.bagId()).isNotEqualTo(previousSnapshot.bagId());
    assertThat(currentSnapshot.items()).isNotEqualTo(previousSnapshot.items());
    assertThat(currentSnapshot.items()).isEqualTo(newBagItems);
    assertThat(currentSnapshot.items()).allMatch(item -> !item.isChecked());
  }

  @Test
  @DisplayName("최근 이동이 없는 경우 기본 스냅샷을 포함한 이동 생성 테스트")
  @Transactional
  void 최근_이동이_없는_경우_기본_스냅샷을_포함한_이동_생성_테스트() {
    // given
    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(basedBagItems);

    // when
    routeCommandService.createRoute(requestBagId1, memberId);

    // then
    Route latestRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).orElseThrow();
    Snapshot snapshot = Snapshot.fromJson(latestRoute.getSnapshot());

    assertThat(snapshot.bagId()).isEqualTo(1);
    assertThat(snapshot.items()).isEqualTo(basedBagItems);
  }
}
