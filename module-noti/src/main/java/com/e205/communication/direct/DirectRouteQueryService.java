package com.e205.communication.direct;

import com.e205.MembersInRouteQuery;
import com.e205.communication.RouteQueryService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DirectRouteQueryService implements RouteQueryService {

  @Override
  public List<Integer> queryMembersInPoints(MembersInRouteQuery query) {
    return List.of();
  }

}
