package com.e205.intg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.e205.TestConfiguration;
import com.e205.command.RouteCreateCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
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

  private static final Integer BAG_1 = 1;
  private static final Integer BAG_2 = 2;

  List<SnapshotItem> basedBagItems;
  List<SnapshotItem> currentBagItems;
  List<SnapshotItem> newBagItems;

  @Autowired
  private RouteCommandService routeCommandService;

  @Autowired
  private RouteRepository routeRepository;

  @MockBean
  private BagItemQueryService bagItemQueryService;

  private Integer memberId;
  private RouteCreateCommand requestBagId1;
  private RouteCreateCommand requestBagId2;

  @BeforeEach
  void setUp() {
    memberId = 1;
    requestBagId1 = new RouteCreateCommand(BAG_1, LocalDateTime.now());
    requestBagId2 = new RouteCreateCommand(BAG_2, LocalDateTime.now());
    assignSnapshotItem();
  }

  @Test
  @DisplayName("이동 생성 시 이전 가방과 현재 가방이 같은 경우 이전 스냅샷 제공 테스트")
  @Transactional
  void 이동_생성시_이전_가방과_현재_가방이_같은_경우_이전_스냅샷_제공_테스트() {
    // given
    Snapshot initSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    routeRepository.save(Route.toEntity(memberId, requestBagId1, Snapshot.toJson(initSnapshot)));
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
  @DisplayName("이동 생성 시 이전 가방과 현재 가방이 다른 경우 기본 스냅샷 제공 테스트")
  @Transactional
  void 이동_생성시_이전_가방과_현재_가방이_다른_경우_기본_스냅샷_제공_테스트() {
    // given
    Snapshot initSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    routeRepository.save(Route.toEntity(memberId, requestBagId1, Snapshot.toJson(initSnapshot)));
    Route previousRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).get();

    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(newBagItems);

    // when
    routeCommandService.createRoute(requestBagId2, memberId);
    Route currentRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).get();
    Snapshot previousSnapshot = Snapshot.fromJson(previousRoute.getSnapshot());
    Snapshot currentSnapshot = Snapshot.fromJson(currentRoute.getSnapshot());

    // then
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
    Route latestRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId).orElseThrow();
    Snapshot snapshot = Snapshot.fromJson(latestRoute.getSnapshot());

    // then
    assertThat(snapshot.bagId()).isEqualTo(1);
    assertThat(snapshot.items()).isEqualTo(basedBagItems);
  }

  @Test
  @DisplayName("스냅샷 수정 테스트")
  @Transactional
  void 스냅샷_수정_테스트() {
    // given
    Snapshot initSnapshot = new Snapshot(requestBagId1.bagId(), basedBagItems);
    Snapshot currentSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    Route route = Route.toEntity(memberId, requestBagId1, Snapshot.toJson(initSnapshot));
    routeRepository.save(route);
    SnapshotUpdateCommand command =
        new SnapshotUpdateCommand(route.getId(), Snapshot.toJson(currentSnapshot));

    // when
    routeCommandService.updateSnapshot(command);
    Route updatedRoute = routeRepository.findById(route.getId()).get();

    // then
    assertThat(updatedRoute).isNotNull();
    assertThat(updatedRoute.getSkip()).isEqualTo('N');
    assertThat(updatedRoute.getSnapshot()).isNotEqualTo(Snapshot.toJson(initSnapshot));
    assertThat(updatedRoute.getSnapshot()).isEqualTo(command.snapshot());
  }

  private void assignSnapshotItem() {
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
  }
}
