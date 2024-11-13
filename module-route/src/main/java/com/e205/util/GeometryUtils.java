package com.e205.util;

import com.e205.dto.TrackPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.springframework.stereotype.Component;

@Component
public class GeometryUtils {

  private static final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);

  public static LineString getLineString(List<TrackPoint> points) {
    return geoFactory.createLineString(points.stream()
        .map(point -> new Coordinate(point.lon(), point.lat()))
        .toArray(Coordinate[]::new)
    );
  }

  public static List<TrackPoint> getPoints(LineString lineString) {
    return Stream.of(lineString.getCoordinates())
        .map(coordinate -> TrackPoint.builder()
            .lat(coordinate.getY())
            .lon(coordinate.getX())
            .build())
        .collect(Collectors.toList());
  }

  public boolean isWithinDistance(Geometry point1, Geometry point2, double maxDistanceMeters) {
    double toMeter = maxDistanceMeters / 111000.0;
    if(point1 == null || point2 == null) return false;
    return DistanceOp.isWithinDistance(point1, point2, toMeter);
  }

  public LineString combineTracks(List<LineString> tracks) {
    return geoFactory.createLineString(tracks.stream()
        .flatMap(lineString -> Stream.of(lineString.getCoordinates()))
        .toArray(Coordinate[]::new));
  }

  public Polygon createLineCirclePolygon(LineString lineString, double radiusMeters) {
    double radiusInDegrees = radiusMeters / 111000.0;
    return (Polygon) lineString.buffer(radiusInDegrees);
  }

  public Polygon createCirclePolygon(double lat, double lon, double radiusMeters) {
    double radiusInDegrees = radiusMeters / 111000.0;
    Coordinate center = new Coordinate(lon, lat);
    return (Polygon) geoFactory.createPoint(center).buffer(radiusInDegrees);
  }

  public static List<TrackPoint> toTrackPoints(LineString lineString) {
    List<TrackPoint> trackPoints = new ArrayList<>();
    if (lineString != null) {
      for (Coordinate coordinate : lineString.getCoordinates()) {
        trackPoints.add(new TrackPoint(coordinate.y, coordinate.x));
      }
    }
    return trackPoints;
  }
}
