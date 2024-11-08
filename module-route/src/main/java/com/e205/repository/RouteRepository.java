package com.e205.repository;

import com.e205.domain.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Integer> {

  @Query("SELECT r "
      + "FROM Route r "
      + "WHERE r.id = :routeId "
      + "AND r.memberId = :memberId")
  Optional<Route> findByIdAndMemberId(
      @Param("routeId") Integer routeId,
      @Param("memberId") Integer memberId
  );

  Optional<Route> findFirstByMemberIdOrderByIdDesc(Integer memberId);

  Optional<Route> findFirstByMemberIdAndIdIsLessThanOrderByIdDesc(
      Integer memberId,
      Integer routeId
  );

  Optional<Route> findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(
      Integer memberId,
      Integer routeId
  );

  Optional<Route> findFirstByMemberIdAndEndedAtIsNull(Integer memberId);

  @Query("SELECT r "
      + "FROM Route r "
      + "WHERE r.memberId = :memberId "
      + "AND DATE_FORMAT(r.createdAt, '%Y-%m-%d') = :date")
  List<Route> findAllByMemberIdAndCreatedAtDate(
      @Param("memberId") Integer memberId,
      @Param("date") LocalDate date
  );

  @Query("SELECT r " +
      "FROM Route r " +
      "WHERE r.memberId = (SELECT memberId FROM Route WHERE id = :startRouteId) " +
      "AND r.id BETWEEN :startRouteId AND :endRouteId")
  List<Route> findRoutesWithinRange(
      @Param("startRouteId") Integer startRouteId,
      @Param("endRouteId") Integer endRouteId
  );

  @Query("SELECT r.memberId "
      + "FROM Route r "
      + "WHERE r.track IS NOT NULL "
      + "AND (ST_Contains(:polygon, ST_StartPoint(r.track)) "
      + "OR ST_Contains(:polygon, ST_EndPoint(r.track))) "
      + "AND (r.createdAt >= :timestamp OR r.endedAt >= :timestamp)")
  Set<Integer> findUsersWithinPolygon(
      @Param("polygon") Polygon polygon,
      @Param("timestamp") LocalDateTime timestamp
  );
}
