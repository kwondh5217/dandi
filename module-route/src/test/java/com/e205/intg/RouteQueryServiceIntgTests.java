package com.e205.intg;

import static com.e205.intg.env.Constant.BAG_ID_1;
import static com.e205.intg.env.Constant.BAG_ID_2;
import static com.e205.intg.env.Constant.MEMBER_ID_1;
import static com.e205.intg.env.Constant.ROUTE_ID_1;
import static com.e205.intg.env.Constant.ROUTE_ID_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.e205.TestConfiguration;
import com.e205.command.RouteCreateCommand;
import com.e205.command.RouteEndCommand;
import com.e205.command.SnapshotUpdateCommand;
import com.e205.payload.RoutePayload;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.dto.TrackPoint;
import com.e205.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import com.e205.service.RouteCommandService;
import com.e205.service.RouteQueryService;
import com.e205.util.GeometryUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfiguration.class)
public class RouteQueryServiceIntgTests {

  List<SnapshotItem> basedBagItems;
  List<SnapshotItem> updatedBagItems;
  List<TrackPoint> trackPoints1;
  List<TrackPoint> trackPoints2;

  RouteCreateCommand requestBag1;
  RouteCreateCommand requestBag2;

  Snapshot snapshot1;
  Snapshot snapshot2;

  @Autowired
  private RouteQueryService queryService;

  @Autowired
  private RouteRepository routeRepository;

  @Autowired
  private RouteCommandService commandService;

  @BeforeEach
  void setUp() {
    requestBag1 = new RouteCreateCommand(BAG_ID_1, LocalDateTime.now());
    requestBag2 = new RouteCreateCommand(BAG_ID_2, LocalDateTime.now());

    basedBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, true),
        new SnapshotItem("반지", "💍", 1, true),
        new SnapshotItem("파우치", "👜", 1, true),
        new SnapshotItem("카드", "💳", 1, true)
    );

    updatedBagItems = List.of(
        new SnapshotItem("지갑", "👛", 1, true),
        new SnapshotItem("반지", "💍", 1, true),
        new SnapshotItem("파우치", "👜", 1, true),
        new SnapshotItem("카드", "💳", 1, true)
    );

    trackPoints1 = List.of(
        TrackPoint.builder().lat(37.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(34.0522).lon(-118.2437).build()
    );

    trackPoints2 = List.of(
        TrackPoint.builder().lat(40.7749).lon(-122.4194).build(),
        TrackPoint.builder().lat(44.0522).lon(-118.2437).build()
    );

    snapshot1 = new Snapshot(BAG_ID_1, basedBagItems);
    SnapshotUpdateCommand sc1 = new SnapshotUpdateCommand(ROUTE_ID_1, snapshot1);
    snapshot2 = new Snapshot(BAG_ID_1, updatedBagItems);
    SnapshotUpdateCommand sc2 = new SnapshotUpdateCommand(ROUTE_ID_2, snapshot2);

    commandService.createRoute(requestBag1, MEMBER_ID_1);
    commandService.updateSnapshot(sc1);
    RouteEndCommand command1 = new RouteEndCommand(ROUTE_ID_1, LocalDateTime.now(), trackPoints1);

    commandService.createRoute(requestBag2, MEMBER_ID_1);
    commandService.updateSnapshot(sc2);
    RouteEndCommand command2 = new RouteEndCommand(ROUTE_ID_2, LocalDateTime.now(), trackPoints2);

    commandService.endRoute(command1);
    commandService.endRoute(command2);
  }

  @Test
  @DisplayName("이동 상세 조회 테스트")
  @Transactional
  void 이동_상세_조회_테스트() {
    // given
    RouteReadQuery query = new RouteReadQuery(ROUTE_ID_1);

    // when
    RoutePayload routePayload = queryService.readRoute(query);

    // then
    assertThat(routePayload.startSnapshot()).isEqualTo(snapshot1);
    assertThat(routePayload.memberId()).isEqualTo(MEMBER_ID_1);
    assertThat(routePayload.track()).isEqualTo(GeometryUtils.getLineString(trackPoints1));
    assertThat(routePayload.endSnapshot()).isEqualTo(snapshot2);
  }
}
