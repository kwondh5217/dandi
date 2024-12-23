package com.e205.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.e205.base.member.command.bag.service.BagQueryService;
import com.e205.events.EventPublisher;
import com.e205.service.DirectRouteCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "route.distance-threshold=800.0"
})
@SpringBootTest
class GeometryUtilsTest {

  @Autowired
  private GeometryUtils geometryUtils;
  private GeometryFactory geoFactory;
  @MockBean
  private DirectRouteCommandService routeCommandService;
  @MockBean
  private EventPublisher eventPublisher;
  @MockBean
  private BagQueryService bagQueryService;

  @BeforeEach
  void setUp() {
    geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
  }

  @Test
  @DisplayName("두 지점이 반경 내 있는 경우 테스트")
  void 두_지점이_반경_내_있는_경우_테스트() {
    // given
    Point point1 = geoFactory.createPoint(new Coordinate(126.9780, 37.5665));
    Point point2 = geoFactory.createPoint(new Coordinate(126.9785, 37.5670));

    // when
    boolean result = geometryUtils.isWithinDistance(point1, point2);

    // then
    assertThat(result).isTrue(); // 1km 내에 있어야 함
  }

  @Test
  @DisplayName("두 지점이 반경 내 없는 경우 테스트")
  void 두_지점이_반경_내_없는_경우_테스트() {
    // given
    Point point1 = geoFactory.createPoint(new Coordinate(126.9780, 37.5665));
    Point point2 = geoFactory.createPoint(new Coordinate(127.0800, 37.6700));

    // when
    boolean result = geometryUtils.isWithinDistance(point1, point2);

    // then
    assertThat(result).isFalse(); // 1km를 초과하여 false여야 함
  }
}
