package com.e205.intg;

import static com.e205.intg.env.Constant.BAG_ID_1;
import static com.e205.intg.env.Constant.BAG_ID_2;
import static com.e205.intg.env.Constant.MEMBER_ID_1;
import static com.e205.intg.env.Constant.ROUTE_ID_1;
import static com.e205.intg.env.Constant.ROUTE_ID_2;
import static com.e205.intg.env.Constant.ROUTE_ID_3;
import static com.e205.intg.env.Constant.ROUTE_ID_4;
import static org.assertj.core.api.Assertions.assertThat;

import com.e205.TestConfiguration;
import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.domain.Route;
import com.e205.dto.RoutePart;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.dto.TrackPoint;
import com.e205.payload.RoutePayload;
import com.e205.payload.RoutesPayload;
import com.e205.query.DailyRouteReadQuery;
import com.e205.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import com.e205.service.RouteCommandService;
import com.e205.service.RouteQueryService;
import com.e205.util.GeometryUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = TestConfiguration.class)
public class RouteQueryServiceIntgTests {

  public List<SnapshotItem> basedBagItems;
  public List<SnapshotItem> updatedBagItems;
  public List<TrackPoint> trackPoints1;
  public List<TrackPoint> trackPoints2;
  public List<TrackPoint> trackPoints3;
  public List<TrackPoint> trackPoints4;
  public RouteCreateCommand requestBag1;
  public RouteCreateCommand requestBag2;
  public RouteCreateCommand requestBag3;
  public RouteCreateCommand requestBag4;
  public Snapshot snapshot1;
  public Snapshot snapshot2;
  public SnapshotUpdateCommand sc1;
  public SnapshotUpdateCommand sc2;
  public SnapshotUpdateCommand sc3;
  public SnapshotUpdateCommand sc4;
  public RouteEndCommand command1;
  public RouteEndCommand command2;
  public RouteEndCommand command3;
  public RouteEndCommand command4;
  LocalDateTime date30 = LocalDateTime.of(2024, 10, 30, 10, 0);
  LocalDateTime date31 = LocalDateTime.of(2024, 10, 31, 10, 0);
  LocalDateTime endDate30 = LocalDateTime.of(2024, 10, 30, 10, 0).plusHours(1);
  LocalDateTime endDate31 = LocalDateTime.of(2024, 10, 31, 10, 0).plusHours(1);
  @Autowired
  private RouteQueryService queryService;

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private RouteCommandService commandService;

  @BeforeEach
  void setUp() {
    initTracksPoints();
    initBagsItems();
    createRouteCommands();
    createRoutes();
  }

  @Test
  @DisplayName("이동 상세 조회 테스트")
  @Transactional
  void 이동_상세_조회_테스트() {
    // given
    RouteReadQuery query = new RouteReadQuery(ROUTE_ID_1);

    // when
    RoutePayload routePayload = queryService.readRoute(query);

    // then
    assertThat(routePayload.startSnapshot()).isEqualTo(snapshot1);
    assertThat(routePayload.memberId()).isEqualTo(MEMBER_ID_1);
    assertThat(routePayload.track()).isEqualTo(GeometryUtils.getLineString(trackPoints1));
    assertThat(routePayload.endSnapshot()).isEqualTo(snapshot2);
  }

  @Test
  @DisplayName("이동 상세 조회 - 시작, 끝 지점 반경 외 테스트")
  @Transactional
  void 이동_상세_조회_시작_끝_반경_외_테스트() {
    // given

    RouteReadQuery query = new RouteReadQuery(ROUTE_ID_1);
    trackPoints2 = List.of(
        TrackPoint.builder().lat(44.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(44.7749).lon(-122.4195).build()
    );
    Route nextRoute = routeRepository.findById(ROUTE_ID_2).get();
    nextRoute.updateTrack(GeometryUtils.getLineString(trackPoints2));
    routeRepository.save(nextRoute);

    // when
    RoutePayload routePayload = queryService.readRoute(query);

    // then
    assertThat(routePayload.track()).isEqualTo(GeometryUtils.getLineString(trackPoints1));
    assertThat(routePayload.endSnapshot()).isNull();
  }

  @Test
  @DisplayName("일일 이동 조회 테스트 - 일일 이동, 다음 이동 아이디 반환")
  @Transactional
  void 일일_이동_조회_테스트() {
    // given
    DailyRouteReadQuery query = new DailyRouteReadQuery(MEMBER_ID_1, LocalDate.of(2024, 10, 30));

    // when
    RoutesPayload routesPayload = queryService.readSpecificDayRoutes(query);

    // then
    // 일일 이동 검증
    List<RoutePart> parts = routesPayload.routeParts();
    assertThat(routesPayload).isNotNull();
    assertThat(parts).hasSize(3);
    assertThat(parts.stream().allMatch(part ->
        part.createdAt().toLocalDate().isEqual(date30.toLocalDate()))
    ).isTrue();

    // 다음 이동 ID 검증
    Integer expectedNextRouteId = routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(
        MEMBER_ID_1, parts.get(parts.size() - 1).id()
    ).get().getId();
    assertThat(routesPayload.nextRouteId()).isEqualTo(expectedNextRouteId);
  }

  private void initTracksPoints() {
    trackPoints1 = List.of(
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(37.7749).lon(-122.4195).build()
    );

    trackPoints2 = List.of(
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(37.7749).lon(-122.4195).build()
    );

    trackPoints3 = List.of(
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(37.7749).lon(-122.4195).build()
    );

    trackPoints4 = List.of(
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build()
    );
  }

  private void initBagsItems() {
    basedBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, true),
        new SnapshotItem("반지", "💍", 1, true),
        new SnapshotItem("파우치", "👜", 1, true),
        new SnapshotItem("카드", "💳", 1, true)
    );

    updatedBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, true),
        new SnapshotItem("반지", "💍", 1, true),
        new SnapshotItem("파우치", "👜", 1, true),
        new SnapshotItem("카드", "💳", 1, true)
    );
  }

  private void createRouteCommands() {
    snapshot1 = new Snapshot(BAG_ID_1, basedBagItems);
    snapshot2 = new Snapshot(BAG_ID_2, updatedBagItems);

    requestBag1 = new RouteCreateCommand(BAG_ID_1, date30);
    requestBag2 = new RouteCreateCommand(BAG_ID_2, date30);
    requestBag3 = new RouteCreateCommand(BAG_ID_2, date30);
    requestBag4 = new RouteCreateCommand(BAG_ID_2, date31);

    sc1 = new SnapshotUpdateCommand(ROUTE_ID_1, snapshot1);
    sc2 = new SnapshotUpdateCommand(ROUTE_ID_2, snapshot2);
    sc3 = new SnapshotUpdateCommand(ROUTE_ID_3, snapshot2);
    sc4 = new SnapshotUpdateCommand(ROUTE_ID_4, snapshot2);

    command1 = new RouteEndCommand(ROUTE_ID_1, endDate30, trackPoints1);
    command2 = new RouteEndCommand(ROUTE_ID_2, endDate30, trackPoints2);
    command3 = new RouteEndCommand(ROUTE_ID_3, endDate30, trackPoints3);
    command4 = new RouteEndCommand(ROUTE_ID_4, endDate31, trackPoints4);
  }

  private void createRoutes() {
    commandService.createRoute(requestBag1, MEMBER_ID_1);
    commandService.createRoute(requestBag2, MEMBER_ID_1);
    commandService.createRoute(requestBag3, MEMBER_ID_1);
    commandService.createRoute(requestBag4, MEMBER_ID_1);

    commandService.updateSnapshot(sc1);
    commandService.updateSnapshot(sc2);
    commandService.updateSnapshot(sc3);
    commandService.updateSnapshot(sc4);

    commandService.endRoute(command1);
    commandService.endRoute(command2);
    commandService.endRoute(command3);
    commandService.endRoute(command4);
  }
}
