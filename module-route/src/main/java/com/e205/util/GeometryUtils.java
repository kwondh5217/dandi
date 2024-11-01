package com.e205.util;

import com.e205.dto.TrackPoint;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class GeometryUtils {

  private static final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);

  public static LineString getLineString(List<TrackPoint> points) {
    return geoFactory.createLineString(points.stream()
        .map(point -> new Coordinate(point.lat(), point.lon()))
        .toArray(Coordinate[]::new)
    );
  }

  public static List<TrackPoint> getPoints(LineString lineString) {
    return Stream.of(lineString.getCoordinates())
        .map(coordinate -> TrackPoint.builder()
            .lat(coordinate.getX())
            .lon(coordinate.getY())
            .build())
        .collect(Collectors.toList());
  }
}
