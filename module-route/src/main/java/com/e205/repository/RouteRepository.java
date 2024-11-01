package com.e205.repository;

import com.e205.domain.Route;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Integer> {

  Optional<Route> findFirstByMemberIdOrderByIdDesc(Integer memberId);

  Optional<Route> findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(
      Integer memberId,
      Integer routeId
  );

  @Query("SELECT r "
      + "FROM Route r "
      + "WHERE r.memberId = :memberId "
      + "AND FUNCTION('FORMATDATETIME', r.createdAt, 'yyyy-MM-dd') = :date")
  List<Route> findAllByMemberIdAndCreatedAtDate(
      @Param("memberId") Integer memberId,
      @Param("date") LocalDate date
  );
}
