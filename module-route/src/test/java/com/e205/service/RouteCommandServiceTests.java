package com.e205.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.e205.domain.Route;
import com.e205.interaction.commands.RouteCreateCommand;
import com.e205.interaction.queries.BagItemQueryService;
import com.e205.repository.RouteRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RouteCommandServiceTests {

  private static final Integer MEMBER_ID = 1;
  private static final Integer BAG_ID = 1;

  @InjectMocks
  private RouteCommandService commandService;

  @Mock
  private RouteRepository routeRepository;

  @Mock
  private BagItemQueryService bagItemQueryService;

  @Test
  @DisplayName("이동 시작 성공 테스트")
  void 이동_시작_성공_테스트() {
    // given
    RouteCreateCommand request = new RouteCreateCommand(BAG_ID, LocalDateTime.now());

    // when
    commandService.createRoute(request, MEMBER_ID);

    // then
    verify(routeRepository).save(any(Route.class));
  }
}
