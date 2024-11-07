package com.e205.service;

import static com.e205.env.TestConstant.MEMBER_ID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.e205.domain.Route;
import com.e205.dto.Snapshot;
import com.e205.payload.RoutePayload;
import com.e205.payload.RoutesPayload;
import com.e205.payload.SnapshotPayload;
import com.e205.query.DailyRouteReadQuery;
import com.e205.query.RouteReadQuery;
import com.e205.query.SnapshotReadQuery;
import com.e205.repository.RouteRepository;
import com.e205.util.GeometryUtils;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-sql/route.sql")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = TestConfiguration.class)
class RouteQueryServiceTests {

  GeometryFactory geometryFactory = new GeometryFactory();

  @Mock
  private RouteRepository routeRepository;

  @Mock
  private GeometryUtils geometryUtils;

  @InjectMocks
  private DirectRouteQueryService routeQueryService;

  @Mock
  private Route route;

  @Mock
  private Route nextRoute;

  @Mock
  private RouteReadQuery routeReadQuery;

  @Mock
  private DailyRouteReadQuery dailyRouteQuery;

  @Mock
  private Route unfinishedRoute;

  private LineString lineString;

  @BeforeEach
  void setUp() {
    lineString = geometryFactory.createLineString(new Coordinate[]{
        new Coordinate(127.0, 37.5),
        new Coordinate(127.1, 37.6)
    });
  }

  @Test
  @DisplayName("이동 상세 조회 시 다음 이동이 없다면 endSnapshot은 null이 된다.")
  void 이동_상세_조회_시_다음_이동이_없으면_endSnapshot은_null_테스트() {
    // given
    Integer routeId = route.getId();
    given(routeReadQuery.routeId()).willReturn(routeId);
    given(routeRepository.findById(routeId)).willReturn(Optional.of(route));
    given(routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(
        route.getMemberId(), routeId)
    ).willReturn(Optional.empty());

    // when
    RoutePayload payload = routeQueryService.readRoute(routeReadQuery);

    // then
    assertThat(payload.nextRouteId()).isNull();
  }

  @Test
  @DisplayName("이동 상세 조회 시 끝나지 않은 이동의 경우 endSnapshot은 null이 된다.")
  void 이동_상세_조회_시_끝나지_않은_이동의_경우_endSnapshot은_null_테스트() {
    // given
    Integer routeId = route.getId();
    given(routeReadQuery.routeId()).willReturn(routeId);
    given(routeRepository.findById(routeId)).willReturn(Optional.of(route));
    given(route.getEndedAt()).willReturn(null); // 이동이 끝나지 않았음을 설정

    // when
    RoutePayload payload = routeQueryService.readRoute(routeReadQuery);

    // then
    assertThat(payload.nextRouteId()).isNull();
  }

  @Test
  @DisplayName("이동 상세 조회 시 조회 된 이동의 끝점과 다음 이동의 시작점이 일정 반경을 벗어나면 endSnapshot은 null 이 된다.")
  void 이동_상세_조회_시_반경_벗어나면_endSnapshot_null_테스트() {
    // given
    Integer routeId = route.getId();
    given(routeReadQuery.routeId()).willReturn(routeId);
    given(routeRepository.findById(routeId)).willReturn(Optional.of(route));
    given(route.getMemberId()).willReturn(MEMBER_ID_1);
    given(routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(MEMBER_ID_1, routeId))
        .willReturn(Optional.of(nextRoute));
    given(geometryUtils.isWithinDistance(route.getTrack(), nextRoute.getTrack(), 1000)).willReturn(
        false);

    // when
    RoutePayload payload = routeQueryService.readRoute(routeReadQuery);

    // then
    assertThat(payload.nextRouteId()).isNull();
  }


  @Test
  @DisplayName("일일 이동 조회 시 해당 날짜에 기록된 이동이 없는 경우 null 반환")
  void 일일_이동_조회_시_해당_날짜에_기록된_이동이_없으면_null_반환_테스트() {
    // given
    LocalDate date = LocalDate.now();
    given(dailyRouteQuery.memberId()).willReturn(MEMBER_ID_1);
    given(dailyRouteQuery.date()).willReturn(date);
    given(routeRepository.findAllByMemberIdAndCreatedAtDate(MEMBER_ID_1, date))
        .willReturn(Collections.emptyList());

    // when
    RoutesPayload result = routeQueryService.readDailyRoute(dailyRouteQuery);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("일일 이동 조회 시 해당 날짜에 끝나지 않은 이동이 있는 경우 nextRouteId 는 null 반환")
  void 일일_이동_조회_시_해당_날짜에_끝나지_않은_이동이_있으면_nextRouteId_null_반환_테스트() {
    // given
    LocalDate date = LocalDate.of(2024, 10, 30);
    given(dailyRouteQuery.memberId()).willReturn(MEMBER_ID_1);
    given(dailyRouteQuery.date()).willReturn(date);
    given(unfinishedRoute.getEndedAt()).willReturn(null);
    given(routeRepository.findAllByMemberIdAndCreatedAtDate(MEMBER_ID_1, date))
        .willReturn(List.of(unfinishedRoute));
    given(unfinishedRoute.getTrack()).willReturn(lineString);

    // when
    RoutesPayload result = routeQueryService.readDailyRoute(dailyRouteQuery);

    // then
    assertThat(result).isNotNull();
    assertThat(result.routeParts()).hasSize(1);
    assertThat(result.nextRouteId()).isNull();
  }

  @Test
  @DisplayName("일일 이동 조회 시 조회 된 이동들의 마지막의 끝점과 다음 이동의 시작점이 일정 반경을 벗어나면 nextRouteId는 null 이 된다.")
  void 일일_이동_조회_시_반경_벗어나면_nextRouteId_null_테스트() {
    // given
    LocalDate date = LocalDate.of(2024, 10, 30);
    given(dailyRouteQuery.memberId()).willReturn(MEMBER_ID_1);
    given(dailyRouteQuery.date()).willReturn(date);
    given(routeRepository.findAllByMemberIdAndCreatedAtDate(MEMBER_ID_1, date))
        .willReturn(List.of(route));
    given(route.getEndedAt()).willReturn(LocalDate.now().atTime(23, 59));
    given(route.getId()).willReturn(1);
    given(route.getTrack()).willReturn(lineString);

    // 다음 이동 설정 및 반경 벗어나는 경우 설정
    given(
        routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(MEMBER_ID_1, route.getId()))
        .willReturn(Optional.of(nextRoute));
    given(geometryUtils.isWithinDistance(route.getTrack(), nextRoute.getTrack(), 1000))
        .willReturn(false);

    // when
    RoutesPayload result = routeQueryService.readDailyRoute(dailyRouteQuery);

    // then
    assertThat(result).isNotNull();
    assertThat(result.routeParts()).hasSize(1);
    assertThat(result.nextRouteId()).isNull();
  }

  @Test
  @DisplayName("스냅샷 조회 성공 테스트")
  void 스냅샷_조회_성공_테스트() {
    // given
    Integer routeId = 1;
    SnapshotReadQuery query = new SnapshotReadQuery(routeId);
    String snapshotJson = """
        {
            "bagId": 1,
            "items": [
                {"name": "지갑", "emoticon": "👛", "type": 1, "isChecked": true},
                {"name": "반지", "emoticon": "💍", "type": 1, "isChecked": true},
                {"name": "파우치", "emoticon": "👜", "type": 1, "isChecked": true},
                {"name": "카드", "emoticon": "💳", "type": 1, "isChecked": true}
            ]
        }
        """;

    Snapshot expectedSnapshot = Snapshot.fromJson(snapshotJson);

    given(routeRepository.findById(routeId)).willReturn(Optional.of(route));
    given(route.getSnapshot()).willReturn(snapshotJson);
    given(route.getSkip()).willReturn('N');

    // when
    SnapshotPayload snapshotPayload = routeQueryService.readSnapshot(query);

    // then
    assertThat(snapshotPayload.snapshot()).isEqualTo(expectedSnapshot);
    assertThat(snapshotPayload.skip()).isEqualTo('N');
  }
}
