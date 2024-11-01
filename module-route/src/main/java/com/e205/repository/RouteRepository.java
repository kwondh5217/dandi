package com.e205.repository;

import com.e205.domain.Route;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Integer> {

  Optional<Route> findFirstByMemberIdOrderByIdDesc(Integer memberId);

  Optional<Route> findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(
      Integer memberId,
      Integer routeId
  );
}
