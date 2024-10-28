package com.e205.communication;

import com.e205.MembersInRouteQuery;
import java.util.List;

public interface RouteQueryService {

  List<Integer> queryMembersInPoints(MembersInRouteQuery query);

}
