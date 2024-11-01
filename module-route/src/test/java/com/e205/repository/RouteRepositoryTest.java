package com.e205.repository;

import static com.e205.intg.env.Constant.MEMBER_ID_1;
import static com.e205.intg.env.Constant.MEMBER_ID_2;
import static org.assertj.core.api.Assertions.assertThat;

import com.e205.domain.Route;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

//@Sql("/test-sql/route.sql")
//@ActiveProfiles(value = "test")
//@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class RouteRepositoryTest {

  @Autowired
  private RouteRepository routeRepository;

  private GeometryFactory geometryFactory;

  LineString track1;
  LineString track2;
  LineString track3;

  Route route1;
  Route route2;
  Route route3;
  Route otherRoute;

  @BeforeEach
  public void setUp() {
    // GeometryFactory 설정 (공간 데이터를 생성하는 데 사용)
    geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    track1 = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.0, 37.5),
        new Coordinate(127.1, 37.6)
    });
    track2 = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.2, 37.7),
        new Coordinate(127.3, 37.8)
    });
    track3 = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.4, 37.9),
        new Coordinate(127.5, 38.0)
    });
  }

  @Test
  @Transactional
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
  @Transactional
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

  private Route insertRoutes() {
    route1 = Route.builder()
        .memberId(MEMBER_ID_1)
        .track(track1)
        .createdAt(LocalDateTime.now().minusDays(2))
        .endedAt(LocalDateTime.now().minusDays(2).plusHours(1))
        .build();

    route2 = Route.builder()
        .memberId(MEMBER_ID_1)
        .track(track2)
        .createdAt(LocalDateTime.now().minusDays(1))
        .endedAt(LocalDateTime.now().minusDays(1).plusHours(1))
        .build();

    route3 = Route.builder()
        .memberId(MEMBER_ID_1)
        .track(track3)
        .createdAt(LocalDateTime.now())
        .endedAt(null)
        .build();

    otherRoute = Route.builder()
        .memberId(MEMBER_ID_2)
        .track(track3)
        .createdAt(LocalDateTime.now())
        .endedAt(null)
        .build();

    routeRepository.save(route1);
    routeRepository.save(otherRoute);
    routeRepository.save(route2);
    routeRepository.save(route3);

    return route3;
  }
}
