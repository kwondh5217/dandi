package com.e205.repository;

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

  private static final Integer MEMBER_ID = 1;

  @Autowired
  private RouteRepository routeRepository;

  private GeometryFactory geometryFactory;

  LineString track1;
  LineString track2;
  LineString track3;

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
    Optional<Route> foundRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(MEMBER_ID);

    // then
    assertThat(foundRoute).isPresent();
    assertThat(foundRoute.get().getCreatedAt()).isEqualTo(latestRoute.getCreatedAt());
    assertThat(foundRoute.get().getTrack()).isEqualTo(latestRoute.getTrack());
  }

  private Route insertRoutes() {
    Route route1 = Route.builder()
        .memberId(MEMBER_ID)
        .track(track1)
        .createdAt(LocalDateTime.now().minusDays(2))
        .endedAt(LocalDateTime.now().minusDays(2).plusHours(1))
        .build();

    Route route2 = Route.builder()
        .memberId(MEMBER_ID)
        .track(track2)
        .createdAt(LocalDateTime.now().minusDays(1))
        .endedAt(LocalDateTime.now().minusDays(1).plusHours(1))
        .build();

    Route latestRoute = Route.builder()
        .memberId(MEMBER_ID)
        .track(track3)
        .createdAt(LocalDateTime.now())
        .endedAt(null)
        .build();

    routeRepository.save(route1);
    routeRepository.save(route2);
    routeRepository.save(latestRoute);

    return latestRoute;
  }
}
