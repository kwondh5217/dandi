package com.e205.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.e205.domain.Route;
import com.e205.payload.RoutePayload;
import com.e205.query.RouteReadQuery;
import com.e205.repository.RouteRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RouteQueryServiceTests {

  @Mock
  private RouteRepository routeRepository;

  @InjectMocks
  private RouteQueryService routeQueryService;

  @Mock
  private Route route;

  @Mock
  private RouteReadQuery query;

  @Test
  @DisplayName("이동 상세 조회 시 다음 이동이 없다면 endSnapshot은 null이 된다.")
  void 이동_상세_조회_시_다음_이동이_없으면_endSnapshot은_null_테스트() {
    // given
    Integer routeId = route.getId();
    given(query.routeId()).willReturn(routeId);
    given(routeRepository.findById(routeId)).willReturn(Optional.of(route));
    given(routeRepository.findFirstByMemberIdAndIdGreaterThanOrderByIdAsc(
        route.getMemberId(), routeId)
    ).willReturn(Optional.empty());

    // when
    RoutePayload payload = routeQueryService.readRoute(query);

    // then
    assertThat(payload.endSnapshot()).isNull();
  }
}
