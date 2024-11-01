package com.e205.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

class GeometryUtilsTest {

  private GeometryUtils geometryUtils;
  private GeometryFactory geoFactory;

  @BeforeEach
  void setUp() {
    geometryUtils = new GeometryUtils();
    geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
  }

  @Test
  @DisplayName("두 지점이 반경 내 있는 경우 테스트")
  void 두_지점이_반경_내_있는_경우_테스트() {
    // given
    Point point1 = geoFactory.createPoint(new Coordinate(37.5665, 126.9780));
    Point point2 = geoFactory.createPoint(new Coordinate(37.5670, 126.9785));

    double maxDistance = 100;

    // when
    boolean result = geometryUtils.isWithinDistance(point1, point2, maxDistance);

    // then
    assertThat(result).isTrue(); // 100m 내에 있어야 함
  }

  @Test
  @DisplayName("두 지점이 반경 내 없는 경우 테스트")
  void 두_지점이_반경_내_없는_경우_테스트() {
    // given
    Point point1 = geoFactory.createPoint(new Coordinate(37.5665, 126.9780));
    Point point2 = geoFactory.createPoint(new Coordinate(37.5700, 126.9800));

    double maxDistance = 100;

    // when
    boolean result = geometryUtils.isWithinDistance(point1, point2, maxDistance);

    // then
    assertThat(result).isFalse(); // 100m를 초과하여 false여야 함
  }
}
