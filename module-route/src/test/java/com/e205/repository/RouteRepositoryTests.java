package com.e205.repository;

import static com.e205.env.TestConstant.MEMBER_ID_1;
import static com.e205.env.TestConstant.MEMBER_ID_2;
import static com.e205.env.TestConstant.MEMBER_ID_3;
import static org.assertj.core.api.Assertions.assertThat;

import com.e205.command.bag.service.BagQueryService;
import com.e205.domain.Route;
import com.e205.events.EventPublisher;
import com.e205.util.GeometryUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
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
@SpringBootTest
class RouteRepositoryTests {

  LineString track1;
  LineString track2;
  LineString track3;
  Route route1;
  Route route2;
  Route route3;
  Route otherRoute1;
  Route otherRoute2;
  @Autowired
  private RouteRepository routeRepository;
  private GeometryFactory geometryFactory;
  @Autowired
  private GeometryUtils geometryUtils;
  @MockBean
  private EventPublisher eventPublisher;
  @MockBean
  private BagQueryService bagQueryService;

  @BeforeEach
  public void setUp() {
    // GeometryFactory 설정 (공간 데이터를 생성하는 데 사용)
    geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    track1 = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.023, 37.5),
        new Coordinate(127.097, 37.6)
    });
    track2 = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(126.759, 37.5),
        new Coordinate(128.0, 37.6)
    });
    track3 = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.6, 38.0),
        new Coordinate(128.0, 38.5)
    });
  }

  @Test
  @DisplayName("최근 이동 가져오기 테스트")
  void 최근_이동_가져오기_테스트() {
    // given
    Route latestRoute = insertRoutes();

    // when
    Optional<Route> foundRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID_1);

    // then
    assertThat(foundRoute).isPresent();
    assertThat(foundRoute.get().getCreatedAt()).isEqualTo(latestRoute.getCreatedAt());
    assertThat(foundRoute.get().getTrack()).isEqualTo(latestRoute.getTrack());
  }

  @Test
  @DisplayName("최근 이동 가져오기 테스트")
  void 최근_종료된_이동_가져오기_테스트() {
    // given
    insertRoutes();

    // when
    Optional<Route> foundRoute = routeRepository.findFirstByMemberIdOrderByEndedAtDesc(MEMBER_ID_1);

    // then
    assertThat(foundRoute).isPresent();
    assertThat(foundRoute.get().getId()).isEqualTo(route2.getId());
    assertThat(foundRoute.get().getCreatedAt()).isEqualTo(route2.getCreatedAt());
    assertThat(foundRoute.get().getTrack()).isEqualTo(route2.getTrack());
  }

  @Test
  @DisplayName("특정 ID보다 작은 최근 경로 조회 테스트")
  void 특정_ID_보다_작은_최근_경로_조회_테스트() {
    // given
    insertRoutes();
    Integer targetRouteId = route2.getId();

    // when
    Optional<Route> foundRoute = routeRepository.findFirstByMemberIdAndIdIsLessThanOrderByIdDesc(
        MEMBER_ID_1, targetRouteId
    );

    // then
    assertThat(foundRoute).isPresent();
    assertThat(foundRoute.get().getId()).isLessThan(targetRouteId);
    assertThat(foundRoute.get().getId()).isEqualTo(route1.getId());
    assertThat(foundRoute.get().getTrack()).isEqualTo(track1);
  }

  @Test
  @DisplayName("현재 이동의 다음 이동 가져오기 테스트")
  void 현재_이동의_다음_이동_가져오기_테스트() {
    // given
    insertRoutes();

    // when
    Optional<Route> nextRoute = routeRepository
        .findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(MEMBER_ID_1, route1.getId());

    // then
    assertThat(nextRoute).isPresent();
    assertThat(nextRoute.get().getId()).isGreaterThan(route1.getId());
    assertThat(nextRoute.get().getTrack()).isEqualTo(track2);
  }

  @Test
  @DisplayName("특정 ID 범위의 경로 조회 테스트")
  void 특정_ID_범위의_경로_조회_테스트() {
    // given
    insertRoutes();
    Integer startRouteId = route1.getId();
    Integer endRouteId = route3.getId();

    // when
    List<Route> routesInRange = routeRepository.findRoutesWithinRange(startRouteId, endRouteId);

    // then
    assertThat(routesInRange).hasSize(3);
    assertThat(routesInRange).extracting("id")
        .containsExactly(route1.getId(), route2.getId(), route3.getId());
  }

  @Test
  @DisplayName("경로 반경 내 사용자 조회 테스트")
  void 경로_반경_내_사용자_조회_테스트() {
    // given
    insertRoutes();

    // 테스트 경로 설정 및 반경 1km Polygon 생성
    LineString testPath = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.0, 37.5),
        new Coordinate(127.1, 37.6)
    });
    Polygon bufferedPolygon = geometryUtils.createLineCirclePolygon(testPath);
    LocalDateTime recentTime = LocalDateTime.now().minusHours(2);

    // when
    Set<Integer> userIds = routeRepository.findUsersWithinPolygon(bufferedPolygon, recentTime);

    // then
    assertThat(userIds).contains(MEMBER_ID_1); // MEMBER_ID_1이 1km 반경 내에 포함되어야 함
    assertThat(userIds).contains(MEMBER_ID_2); // MEMBER_ID_2도 1km 반경 내에 포함되어야 함
    assertThat(userIds).doesNotContain(MEMBER_ID_3); // MEMBER_ID_3은 반경 내에 포함되지 않아야 함
  }

  private Route insertRoutes() {
    route1 = Route.builder()
        .memberId(MEMBER_ID_1)
        .track(track1)
        .radiusTrack(geometryUtils.createLineCirclePolygon(track1))
        .createdAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now().plusMinutes(40))
        .build();

    route2 = Route.builder()
        .memberId(MEMBER_ID_1)
        .track(track2)
        .radiusTrack(geometryUtils.createLineCirclePolygon(track2))
        .createdAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now().plusHours(1))
        .build();

    route3 = Route.builder()
        .memberId(MEMBER_ID_1)
        .track(track2)
        .radiusTrack(geometryUtils.createLineCirclePolygon(track2))
        .createdAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now().plusMinutes(40))
        .build();

    otherRoute1 = Route.builder()
        .memberId(MEMBER_ID_2)
        .track(track1)
        .radiusTrack(geometryUtils.createLineCirclePolygon(track1))
        .createdAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now().plusMinutes(10))
        .build();

    otherRoute2 = Route.builder()
        .memberId(MEMBER_ID_3)
        .track(track3)
        .radiusTrack(geometryUtils.createLineCirclePolygon(track3))
        .createdAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now().plusMinutes(10))
        .build();

    routeRepository.save(route1);
    routeRepository.save(otherRoute1);
    routeRepository.save(route2);
    routeRepository.save(otherRoute2);
    routeRepository.save(route3);

    return route3;
  }
}
