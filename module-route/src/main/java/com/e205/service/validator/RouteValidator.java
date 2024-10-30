package com.e205.service.validator;

import com.e205.domain.Route;
import com.e205.exception.RouteError;
import com.e205.exception.RouteException;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

  public void validateEndedRoute(Route route) {
    if (route.getEndedAt() != null) {
      throw new RouteException(RouteError.ENDED_ROUTE);
    }
  }
}
