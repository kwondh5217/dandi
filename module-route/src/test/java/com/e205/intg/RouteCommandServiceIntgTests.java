package com.e205.intg;

import static com.e205.env.TestConstant.BAG_ID_1;
import static com.e205.env.TestConstant.BAG_ID_2;
import static com.e205.env.TestConstant.MEMBER_ID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.e205.TestConfiguration;
import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.dto.TrackPoint;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.repository.RouteRepository;
import com.e205.service.DirectRouteCommandService;
import com.e205.util.GeometryUtils;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Sql("/test-sql/route.sql")
@ActiveProfiles(value = "test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest(classes = TestConfiguration.class)
public class RouteCommandServiceIntgTests {

  List<SnapshotItem> basedBagItems;
  List<SnapshotItem> currentBagItems;
  List<SnapshotItem> newBagItems;

  RouteCreateCommand requestBagId1;
  RouteCreateCommand requestBagId2;

  @Autowired
  private DirectRouteCommandService routeCommandService;

  @Autowired
  private RouteRepository routeRepository;

  @MockBean
  private BagItemQueryService bagItemQueryService;

  @BeforeEach
  void setUp() {
    requestBagId1 = new RouteCreateCommand(BAG_ID_1, LocalDateTime.now());
    requestBagId2 = new RouteCreateCommand(BAG_ID_2, LocalDateTime.now());
    assignSnapshotItem();
  }

  @Test
  @DisplayName("이동 생성 시 이전 가방과 현재 가방이 같은 경우 이전 스냅샷 제공 테스트")
  void 이동_생성시_이전_가방과_현재_가방이_같은_경우_이전_스냅샷_제공_테스트() {
    // given
    Snapshot initSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    routeRepository.save(Route.toEntity(MEMBER_ID_1, requestBagId1, Snapshot.toJson(initSnapshot)));
    Route previousRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID_1).get();

    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(currentBagItems);

    // when
    routeCommandService.createRoute(requestBagId1, MEMBER_ID_1);
    Route currentRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID_1).get();

    // then
    Snapshot previousSnapshot = Snapshot.fromJson(previousRoute.getSnapshot());
    Snapshot currentSnapshot = Snapshot.fromJson(currentRoute.getSnapshot());

    assertThat(currentSnapshot.bagId()).isEqualTo(previousSnapshot.bagId());
    assertThat(currentSnapshot.items()).isEqualTo(previousSnapshot.items());
  }

  @Test
  @DisplayName("이동 생성 시 이전 가방과 현재 가방이 다른 경우 기본 스냅샷 제공 테스트")
  void 이동_생성시_이전_가방과_현재_가방이_다른_경우_기본_스냅샷_제공_테스트() {
    // given
    Snapshot initSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    routeRepository.save(Route.toEntity(MEMBER_ID_1, requestBagId1, Snapshot.toJson(initSnapshot)));
    Route previousRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID_1).get();

    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(newBagItems);

    // when
    routeCommandService.createRoute(requestBagId2, MEMBER_ID_1);
    Route currentRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID_1).get();
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
  void 최근_이동이_없는_경우_기본_스냅샷을_포함한_이동_생성_테스트() {
    // given
    given(bagItemQueryService.bagItemsOfMember(any())).willReturn(basedBagItems);

    // when
    routeCommandService.createRoute(requestBagId1, MEMBER_ID_1);
    Route latestRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID_1).orElseThrow();
    Snapshot snapshot = Snapshot.fromJson(latestRoute.getSnapshot());

    // then
    assertThat(snapshot.bagId()).isEqualTo(1);
    assertThat(snapshot.items()).isEqualTo(basedBagItems);
  }

  @Test
  @DisplayName("스냅샷 수정 테스트")
  void 스냅샷_수정_테스트() {
    // given
    Snapshot initSnapshot = new Snapshot(requestBagId1.bagId(), basedBagItems);
    Snapshot currentSnapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    Route route = Route.toEntity(MEMBER_ID_1, requestBagId1, Snapshot.toJson(initSnapshot));
    routeRepository.save(route);
    SnapshotUpdateCommand command =
        new SnapshotUpdateCommand(route.getId(), currentSnapshot);

    // when
    routeCommandService.updateSnapshot(command);
    Route updatedRoute = routeRepository.findById(route.getId()).get();

    // then
    assertThat(updatedRoute).isNotNull();
    assertThat(updatedRoute.getSkip()).isEqualTo('N');
    assertThat(updatedRoute.getSnapshot()).isNotEqualTo(Snapshot.toJson(initSnapshot));
    assertThat(updatedRoute.getSnapshot()).isEqualTo(Snapshot.toJson(command.snapshot()));
  }

  @Test
  @DisplayName("이동 종료 테스트")
  void 이동_종료_테스트() {
    // given
    Snapshot snapshot = new Snapshot(requestBagId1.bagId(), currentBagItems);
    Route route = routeRepository.save(
        Route.toEntity(MEMBER_ID_1, requestBagId1, Snapshot.toJson(snapshot))
    );
    List<TrackPoint> trackPoints = List.of(
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(34.0522).lon(-118.2437).build()
    );
    RouteEndCommand command = new RouteEndCommand(route.getId(), LocalDateTime.now(), trackPoints);

    // when
    routeCommandService.endRoute(command);
    Route endedRoute = routeRepository.findById(route.getId()).orElseThrow();
    List<TrackPoint> savedTrackPoints = GeometryUtils.getPoints(endedRoute.getTrack());

    // then
    assertThat(endedRoute.getEndedAt()).isNotNull();
    assertThat(savedTrackPoints).isNotNull();
    assertThat(savedTrackPoints).containsExactlyElementsOf(trackPoints);
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
