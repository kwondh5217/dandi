package com.e205.service;

import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.interaction.commands.RouteCreateCommand;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.interaction.queries.BagItemsOfMemberQuery;
import com.e205.repository.RouteRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RouteCommandService {

  private final RouteRepository routeRepository;

  private final BagItemQueryService bagItemQueryService;

  public void createRoute(RouteCreateCommand request, Integer memberId) {
    Optional<Route> currentRoute = routeRepository.findFirstByMemberIdOrderByIdDesc(memberId);

    String determinedSnapshot = currentRoute.map(findRoute -> {
          Snapshot defaultSnapshot = loadBaseSnapshot(request, memberId);
          Snapshot currentSnapshot = loadCurrentSnapshot(findRoute);
          return Route.determineSnapshot(request, defaultSnapshot, currentSnapshot);
        })
        .orElseGet(() -> {
          return Snapshot.toJson(loadBaseSnapshot(request, memberId));
        });

    routeRepository.save(Route.toEntity(memberId, request, determinedSnapshot));
  }

  private Snapshot loadBaseSnapshot(RouteCreateCommand request, Integer memberId) {
    BagItemsOfMemberQuery query = new BagItemsOfMemberQuery(memberId, request.bagId());
    List<SnapshotItem> snapshotItems = bagItemQueryService.bagItemsOfMember(query);

    return new Snapshot(request.bagId(), snapshotItems);
  }

  private Snapshot loadCurrentSnapshot(Route previousRoute) {
    return Snapshot.fromJson(previousRoute.getSnapshot());
  }
}
