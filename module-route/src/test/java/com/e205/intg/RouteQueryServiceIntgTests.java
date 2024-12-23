package com.e205.intg;

import static com.e205.env.TestConstant.BAG_ID_1;
import static com.e205.env.TestConstant.BAG_ID_2;
import static com.e205.env.TestConstant.MEMBER_ID_1;
import static com.e205.env.TestConstant.MEMBER_ID_2;
import static com.e205.env.TestConstant.MEMBER_ID_3;
import static com.e205.env.TestConstant.MEMBER_ID_4;
import static com.e205.env.TestConstant.ROUTE_ID_1;
import static com.e205.env.TestConstant.ROUTE_ID_2;
import static com.e205.env.TestConstant.ROUTE_ID_3;
import static com.e205.env.TestConstant.ROUTE_ID_4;
import static com.e205.env.TestConstant.ROUTE_ID_5;
import static com.e205.env.TestConstant.ROUTE_ID_6;
import static com.e205.env.TestConstant.ROUTE_ID_7;
import static org.assertj.core.api.Assertions.assertThat;

import com.e205.TestConfiguration;
import com.e205.base.member.command.bag.service.BagQueryService;
import com.e205.domain.Route;
import com.e205.base.route.dto.RoutePart;
import com.e205.base.route.dto.Snapshot;
import com.e205.base.route.dto.SnapshotItem;
import com.e205.base.route.dto.TrackPoint;
import com.e205.events.EventPublisher;
import com.e205.base.route.payload.RoutePayload;
import com.e205.base.route.payload.RoutesPayload;
import com.e205.base.route.query.DailyRouteReadQuery;
import com.e205.base.route.query.MembersInPointQuery;
import com.e205.base.route.query.MembersInRouteQuery;
import com.e205.base.route.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import com.e205.base.route.service.RouteQueryService;
import com.e205.util.GeometryUtils;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
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
public class RouteQueryServiceIntgTests {

  public List<SnapshotItem> basedBagItems;
  public List<SnapshotItem> updatedBagItems;
  public List<TrackPoint> trackPoints1;
  public List<TrackPoint> trackPoints2;
  public List<TrackPoint> trackPoints3;
  public List<TrackPoint> trackPoints4;
  public Snapshot snapshot1;
  public Snapshot snapshot2;
  public Route route1;
  public Route route2;
  public Route route3;
  public Route route4;
  public Route route5;
  public Route withinPolygonRoute;
  public Route noneWithinPolygonRoute;

  LocalDateTime dateNow = LocalDateTime.now();
  LocalDateTime dateTomorrow = LocalDateTime.now().plusDays(1);
  LocalDateTime endDateNow = LocalDateTime.now().minusMinutes(30);
  LocalDateTime endDateTomorrow = LocalDateTime.now().plusDays(1).plusMinutes(30);

  @Autowired
  private RouteQueryService queryService;

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private GeometryUtils geometryUtils;

  @MockBean
  private BagQueryService bagQueryService;

  @MockBean
  private EventPublisher eventPublisher;

  @BeforeEach
  void setUp() {
    entityManager.createNativeQuery("ALTER TABLE route AUTO_INCREMENT = 1").executeUpdate();
    createSpatialIndexIfNotExists();

    initTracksPoints();
    initBagsItems();
    createSnapshot();
    createRoutes();
  }

  @Test
  @DisplayName("이동 상세 조회 테스트")
  void 이동_상세_조회_테스트() {
    // given
    RouteReadQuery query = new RouteReadQuery(MEMBER_ID_1, ROUTE_ID_1);

    // when
    RoutePayload routePayload = queryService.readRoute(query);

    // then
    assertThat(routePayload.startSnapshot()).isEqualTo(snapshot1);
    assertThat(routePayload.memberId()).isEqualTo(MEMBER_ID_1);
    assertThat(routePayload.track()).isEqualTo(trackPoints1);
    assertThat(routePayload.previousRouteId()).isNull();
    assertThat(routePayload.nextRouteId()).isEqualTo(ROUTE_ID_2);
  }

  @Test
  @DisplayName("이동 상세 조회 - 시작, 끝 지점 반경 외 테스트")
  void 이동_상세_조회_시작_끝_반경_외_테스트() {
    // given
    RouteReadQuery query = new RouteReadQuery(MEMBER_ID_1, ROUTE_ID_1);
    trackPoints2 = List.of(
        TrackPoint.builder().lat(44.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(44.7749).lon(-122.4195).build()
    );

    routeRepository.findById(ROUTE_ID_2).ifPresent(
        (route) -> {
          route.updateTrack(geometryUtils.getLineString(trackPoints2));
          routeRepository.save(route);
        }
    );

    // when
    RoutePayload routePayload = queryService.readRoute(query);

    // then
    assertThat(routePayload.track()).isEqualTo(trackPoints1);
    assertThat(routePayload.nextRouteId()).isNotNull();
    assertThat(routePayload.nextSnapshot()).isNull();
  }

  @Test
  @DisplayName("일일 이동 조회 테스트 - 일일 이동, 다음 이동 아이디 반환")
  void 일일_이동_조회_테스트() {
    // given
    DailyRouteReadQuery query = new DailyRouteReadQuery(MEMBER_ID_1, dateNow.toLocalDate());

    // when
    RoutesPayload routesPayload = queryService.readDailyRoute(query);

    // then
    // 일일 이동 검증, 오늘날의 이동
    List<RoutePart> parts = routesPayload.routeParts();
    assertThat(routesPayload).isNotNull();
    assertThat(parts).hasSize(3);
    assertThat(parts.stream().allMatch(part ->
        part.createdAt().toLocalDate().isEqual(dateNow.toLocalDate()))
    ).isTrue();

    // 다음 이동 ID 검증
    Integer expectedNextRouteId = route4.getId();
    assertThat(routesPayload.nextRouteId()).isEqualTo(expectedNextRouteId);
  }

  @ParameterizedTest
  @CsvSource({
      "1440, true", // 최근 1일, 사용자 4 포함
      "360, false" // 최근 6시간, 사용자 4 미포함
  })
  @DisplayName("특정 기간 내 경로 사용자 조회 테스트")
  void 특정_기간_내_경로_사용자_조회_테스트(int minutesAgo, boolean expectedResult) {
    // given
    LocalDateTime since = LocalDateTime.now().minusMinutes(minutesAgo);
    MembersInRouteQuery query = new MembersInRouteQuery(MEMBER_ID_1, ROUTE_ID_1, ROUTE_ID_4, since);

    // when
    List<Integer> userIds = queryService.findUserIdsNearPath(query);

    // then
    assertThat(userIds).contains(MEMBER_ID_2);
    assertThat(userIds).doesNotContain(MEMBER_ID_3);
    if (expectedResult) {
      assertThat(userIds).contains(MEMBER_ID_4);
    } else {
      assertThat(userIds).doesNotContain(MEMBER_ID_4);
    }
  }

  @ParameterizedTest
  @CsvSource({
      // 사용자 1은 현재시간, 사용자 2는 5시간전에 해당 좌표의 이동이 있음
      "37.7749, 127.0, 6, '1,2'", // 최근 6시간 이내, 예상되는 사용자 ID 1, 2
      "37.7749, 127.0, 3, '1'", // 최근 1시간 이내, 예상되는 사용자 ID 1

      // lat : 39.7749, lon : 127.1 좌표에는 사용자 3의 이동밖에 없음
      "38.7749, 128.0, 3, '3'" // 좌표 39.7749, 127.1 반경 내, 예상되는 사용자 ID 3
  })
  @DisplayName("특정 좌표 반경 내의 사용자를 시간 기준으로 조회 테스트")
  void 특정_좌표_반경_내_사용자_조회_테스트(double lat, double lon, int subtractionTime, String ids) {
    // given
    MembersInPointQuery query = MembersInPointQuery.builder()
        .lat(lat)
        .lon(lon)
        .subtractionTime(subtractionTime)
        .build();

    List<Integer> expectedIds = Stream.of(ids.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    // when
    List<Integer> userIds = queryService.findUserIdsNearPoint(query);

    // then
    assertThat(userIds).containsAll(expectedIds);
  }

  private void initTracksPoints() {
    // TrackPoints 1, 2, 3 - 1km 내에서 가까운 위치에 1000개의 TrackPoint 생성
    trackPoints1 = generateTrackPoints(37.7749, 127.0);
    trackPoints2 = generateTrackPoints(37.7750, 127.01);
    trackPoints3 = generateTrackPoints(37.7751, 127.02);

    // TrackPoints 4 - 100km 떨어진 위치에서 1000개의 TrackPoint 생성
    trackPoints4 = generateTrackPoints(38.7749, 128.0);
  }

  private List<TrackPoint> generateTrackPoints(double baseLat, double baseLon) {
    List<TrackPoint> trackPoints = new ArrayList<>();
    for (int i = 0; i < 500; i++) {
      double lat = baseLat + (Math.random() - 0.5) * 0.01;
      double lon = baseLon + (Math.random() - 0.5) * 0.01;
      trackPoints.add(TrackPoint.builder().lat(lat).lon(lon).build());
    }
    return trackPoints;
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

  private void createSnapshot() {
    snapshot1 = new Snapshot(BAG_ID_1, basedBagItems);
    snapshot2 = new Snapshot(BAG_ID_2, updatedBagItems);
  }

  private void createRoutes() {
    long startTime = System.nanoTime();
    LineString track1 = geometryUtils.getLineString(trackPoints1);
    LineString track2 = geometryUtils.getLineString(trackPoints2);
    LineString track3 = geometryUtils.getLineString(trackPoints3);
    LineString track4 = geometryUtils.getLineString(trackPoints4);

    LineString filteredTrack1 = geometryUtils.filterTrackPoints(track1);
    LineString filteredTrack2 = geometryUtils.filterTrackPoints(track2);
    LineString filteredTrack3 = geometryUtils.filterTrackPoints(track3);
    LineString filteredTrack4 = geometryUtils.filterTrackPoints(track4);

    Polygon radiusTrack1 = geometryUtils.createLineCirclePolygon(filteredTrack1);
    Polygon radiusTrack2 = geometryUtils.createLineCirclePolygon(filteredTrack2);
    Polygon radiusTrack3 = geometryUtils.createLineCirclePolygon(filteredTrack3);
    Polygon radiusTrack4 = geometryUtils.createLineCirclePolygon(filteredTrack4);

    route1 = createRoute(
        ROUTE_ID_1, MEMBER_ID_1,
        track1,
        radiusTrack1,
        snapshot1,
        dateNow,
        endDateNow
    );

    route2 = createRoute(
        ROUTE_ID_2, MEMBER_ID_1,
        track2,
        radiusTrack2,
        snapshot2,
        dateNow,
        endDateNow
    );

    route3 = createRoute(
        ROUTE_ID_3, MEMBER_ID_1,
        track3,
        radiusTrack3,
        snapshot1,
        dateNow,
        endDateNow
    );

    route4 = createRoute(
        ROUTE_ID_4, MEMBER_ID_1,
        track3,
        radiusTrack3,
        snapshot2,
        dateTomorrow,
        endDateTomorrow
    );

    route5 = createRoute(
        ROUTE_ID_7, MEMBER_ID_4,
        track2,
        radiusTrack2,
        snapshot2,
        dateNow.minusHours(8),
        endDateNow.minusHours(7)
    );

    withinPolygonRoute = createRoute(
        ROUTE_ID_5, MEMBER_ID_2,
        track2,
        radiusTrack2,
        snapshot2,
        dateNow.minusHours(5),
        endDateNow.minusHours(4)
    );

    noneWithinPolygonRoute = createRoute(
        ROUTE_ID_6, MEMBER_ID_3,
        track4,
        radiusTrack4,
        snapshot2,
        dateNow,
        endDateNow
    );

    routeRepository.save(route1);
    routeRepository.save(route2);
    routeRepository.save(route3);
    routeRepository.save(route4);
    routeRepository.save(withinPolygonRoute);
    routeRepository.save(noneWithinPolygonRoute);
    routeRepository.save(route5);

    long endTime = System.nanoTime();
    long duration = endTime - startTime;
    System.out.println("createRoutes 실행 시간: " + duration / 1_000_000 + " ms");
  }

  private Route createRoute(
      Integer id, Integer memberId, LineString track, Polygon radiusTrack, Snapshot snapshot,
      LocalDateTime createdAt, LocalDateTime endedAt) {
    return Route.builder()
        .id(id)
        .memberId(memberId)
        .track(track)
        .radiusTrack(radiusTrack)
        .skip('N')
        .snapshot(Snapshot.toJson(snapshot))
        .createdAt(createdAt)
        .endedAt(endedAt)
        .build();
  }

  private void createSpatialIndexIfNotExists() {
    Long indexCount = (Long) entityManager.createNativeQuery(
        "SELECT COUNT(*) " +
            "FROM INFORMATION_SCHEMA.STATISTICS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'route' " +
            "AND INDEX_NAME = 'idx_radius_track'"
    ).getSingleResult();

    // 인덱스가 없으면 생성
    if (indexCount == 0) {
      entityManager.createNativeQuery(
          "CREATE SPATIAL INDEX idx_radius_track ON route (radius_track)"
      ).executeUpdate();
    }
  }
}
