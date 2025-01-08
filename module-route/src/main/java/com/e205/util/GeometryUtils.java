package com.e205.util;

import com.e205.base.route.dto.TrackPoint;
import com.e205.exception.GlobalException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeometryUtils {

  @Value("${route.max-radius}")
  private double radius;

  @Value("${route.distance-threshold}")
  private double threshold;

  private static final GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(), 4326);
  private static final int LIMIT_SEQUENTIAL = 10000;


  public LineString getLineString(List<TrackPoint> points) {
    Stream<TrackPoint> pointStream = points.size() > LIMIT_SEQUENTIAL
        ? points.parallelStream()
        : points.stream();

    return geoFactory.createLineString(pointStream
        .map(point -> new Coordinate(point.lon(), point.lat()))
        .toArray(Coordinate[]::new)
    );
  }

  public List<TrackPoint> getPoints(LineString lineString) {
    Coordinate[] coordinatesArray = lineString.getCoordinates();
    Stream<Coordinate> coordinatesStream = coordinatesArray.length > LIMIT_SEQUENTIAL
        ? Stream.of(coordinatesArray).parallel()
        : Stream.of(coordinatesArray);

    return coordinatesStream
        .map(coordinate -> TrackPoint.builder()
            .lat(coordinate.getY())
            .lon(coordinate.getX())
            .build())
        .collect(Collectors.toList());
  }

  public boolean isWithinDistance(Geometry point1, Geometry point2) {
    double radiusInDegrees = radius / 111000.0;
    if (point1 == null || point2 == null) {
      return false;
    }
    return DistanceOp.isWithinDistance(point1, point2, radiusInDegrees);
  }

  public Polygon createLineCirclePolygon(LineString lineString) {
    double radiusInDegrees = radius / 111000.0;
    return (Polygon) lineString.buffer(radiusInDegrees);
  }

  public Polygon createCirclePolygon(double lat, double lon) {
    double radiusInDegrees = radius / 111000.0;
    Coordinate center = new Coordinate(lon, lat);
    return (Polygon) geoFactory.createPoint(center).buffer(radiusInDegrees);
  }

  public Polygon combinePolygons(List<Polygon> polygons) {
    GeometryCollection geometryCollection = new GeometryCollection(
        polygons.toArray(new Geometry[0]), geoFactory);
    Geometry convexHull = geometryCollection.convexHull();
    if (convexHull instanceof Polygon) {
      return (Polygon) convexHull;
    }
    throw new GlobalException("E206");
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

  public LineString createEmptyLineString() {
    return geoFactory.createLineString(new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(0, 0),
    });
  }

  public Polygon createEmptyPolygon() {
    return geoFactory.createPolygon(new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0)
    });
  }

  public LineString filterTrackPoints(LineString originalTrack) {
    List<Point> filteredPoints = new ArrayList<>();
    double distance = 0.0;

    Point point1 = originalTrack.getPointN(0);
    filteredPoints.add(point1);

    for (int i = 1; i < originalTrack.getNumPoints()-1; i++) {
      Point point2 = originalTrack.getPointN(i);
      distance += point1.distance(point2);

      if (distance < threshold) {
        continue;
      }

      filteredPoints.add(point2);
      distance = 0.0;
      point1 = point2;
    }

    Point point = originalTrack.getPointN(originalTrack.getNumPoints()-1);
    filteredPoints.add(point);

    return geoFactory.createLineString(filteredPoints.stream()
        .map(Point::getCoordinate)
        .toArray(Coordinate[]::new)
    );
  }

  /*public LineString combineTracks(List<LineString> tracks) {
    return geoFactory.createLineString(tracks.stream()
        .flatMap(lineString -> Stream.of(lineString.getCoordinates()))
        .toArray(Coordinate[]::new));
  }*/
}
