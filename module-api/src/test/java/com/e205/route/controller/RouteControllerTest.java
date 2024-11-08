package com.e205.route.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e205.auth.helper.AuthHelper;
import com.e205.dto.Snapshot;
import com.e205.dto.SnapshotItem;
import com.e205.route.dto.Point;
import com.e205.route.dto.RouteSummary;
import com.e205.route.dto.command.RouteCreateRequest;
import com.e205.route.dto.command.RouteEndRequest;
import com.e205.route.dto.command.SnapshotUpdateRequest;
import com.e205.route.dto.query.DailyRouteResponse;
import com.e205.route.dto.query.RouteDetailResponse;
import com.e205.route.dto.query.SnapshotDetailResponse;
import com.e205.route.service.RouteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

  private static final Integer MOCK_MEMBER_ID = 1;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private RouteService routeService;

  @Mock
  private AuthHelper authHelper;

  @InjectMocks
  private RouteController routeController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
  }

  @Test
  @DisplayName("이동 생성 요청 성공 테스트")
  void 이동_생성_요청_성공_테스트() throws Exception {
    // given
    RouteCreateRequest request = new RouteCreateRequest(2);
    given(authHelper.getMemberId()).willReturn(MOCK_MEMBER_ID);

    // when
    ResultActions perform = mockMvc.perform(post("/routes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    perform.andExpect(status().isCreated());
    verify(authHelper).getMemberId();
    verify(routeService).createRoute(request, MOCK_MEMBER_ID);
  }

  @Test
  @DisplayName("스냅샷 업데이트 요청 성공 테스트")
  void 스냅샷_업데이트_요청_성공_테스트() throws Exception {
    // given
    Integer routeId = 1;

    SnapshotItem item1 = new SnapshotItem("지갑", "👛", 1, true);
    SnapshotItem item2 = new SnapshotItem("반지", "💍", 1, false);
    SnapshotItem item3 = new SnapshotItem("파우치", "👜", 1, true);
    SnapshotItem item4 = new SnapshotItem("카드", "💳", 1, true);

    Snapshot snapshot = new Snapshot(1, List.of(item1, item2, item3, item4));
    SnapshotUpdateRequest request = new SnapshotUpdateRequest(snapshot);

    given(authHelper.getMemberId()).willReturn(MOCK_MEMBER_ID);

    // when
    mockMvc.perform(patch("/routes/{routeId}/snapshot", routeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // then
    verify(routeService).updateSnapshot(MOCK_MEMBER_ID, routeId, request);
  }

  @Test
  @DisplayName("이동 종료 요청 성공 테스트")
  void 이동_종료_요청_성공_테스트() throws Exception {
    // given
    Integer routeId = 1;

    RouteEndRequest request = new RouteEndRequest(List.of(
        new Point(37.7749, -122.4194),
        new Point(37.7750, -122.4195)
    ));

    given(authHelper.getMemberId()).willReturn(MOCK_MEMBER_ID);

    // when
    mockMvc.perform(patch("/routes/{routeId}", routeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // then
    verify(routeService).endRoute(MOCK_MEMBER_ID, routeId, request);
  }

  @Test
  @DisplayName("일일 이동 조회 요청 성공 테스트")
  void 일일_이동_조회_요청_성공_테스트() throws Exception {
    // given
    LocalDate date = LocalDate.of(2024, 10, 23);
    DailyRouteResponse response = new DailyRouteResponse(List.of(), null);

    given(authHelper.getMemberId()).willReturn(MOCK_MEMBER_ID);
    given(routeService.readDailyRoute(MOCK_MEMBER_ID, date)).willReturn(response);

    // when
    mockMvc.perform(get("/routes")
            .param("date", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(authHelper).getMemberId();
    verify(routeService).readDailyRoute(MOCK_MEMBER_ID, date);
  }

  @Test
  @DisplayName("이동 상세 조회 요청 성공 테스트")
  void 이동_상세_조회_요청_성공_테스트() throws Exception {
    // given
    Integer routeId = 1;
    RouteDetailResponse response = RouteDetailResponse.builder().build();

    given(authHelper.getMemberId()).willReturn(MOCK_MEMBER_ID);
    given(routeService.readRoute(MOCK_MEMBER_ID, routeId)).willReturn(response);

    // when
    mockMvc.perform(get("/routes/{routeId}", routeId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(authHelper).getMemberId();
    verify(routeService).readRoute(MOCK_MEMBER_ID, routeId);
  }

  @Test
  @DisplayName("스냅샷 상세 조회 요청 성공 테스트")
  void 스냅샷_상세_조회_요청_성공_테스트() throws Exception {
    // given
    Integer routeId = 1;
    SnapshotDetailResponse response = SnapshotDetailResponse.builder().build();

    given(routeService.readSnapshot(MOCK_MEMBER_ID, routeId)).willReturn(response);
    given(authHelper.getMemberId()).willReturn(MOCK_MEMBER_ID);

    // when
    mockMvc.perform(get("/routes/{routeId}/snapshot", routeId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    verify(routeService).readSnapshot(MOCK_MEMBER_ID, routeId);
  }
}
