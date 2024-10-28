package com.e205;

import com.e205.annotation.CommandMethod;
import com.e205.annotation.EventMethod;
import com.e205.annotation.QueryMethod;
import com.e205.commands.Command;
import com.e205.commands.CommandDispatcher;
import com.e205.events.Event;
import com.e205.events.EventPublisher;
import com.e205.querys.Query;
import com.e205.querys.QueryDispatcher;
import com.e205.service.CommandService;
import com.e205.service.EventService;
import com.e205.service.QueryService;
import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationDispatcher implements CommandDispatcher, EventPublisher, QueryDispatcher {

  private final Map<String, Method> commandHandlersMethod = new HashMap<>();
  private final Map<String, Method> queryHandlersMethod = new HashMap<>();
  private final Map<String, Method> eventHandlersMethod = new HashMap<>();

  private final CommandService commandService;
  private final QueryService queryService;
  private final EventService eventService;

  @PostConstruct
  public void init() {
    // 커맨드 핸들러 메서드 등록
    for (Method method : CommandService.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(CommandMethod.class)) {
        CommandMethod annotation = method.getAnnotation(CommandMethod.class);
        commandHandlersMethod.put(annotation.type(), method);
      }
    }

    // 쿼리 핸들러 메서드 등록
    for (Method method : QueryService.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(QueryMethod.class)) {
        QueryMethod annotation = method.getAnnotation(QueryMethod.class);
        queryHandlersMethod.put(annotation.type(), method);
      }
    }

    // 이벤트 핸들러 메서드 등록
    for (Method method : EventService.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(EventMethod.class)) {
        EventMethod annotation = method.getAnnotation(EventMethod.class);
        eventHandlersMethod.put(annotation.type(), method);
      }
    }
  }

  public void dispatchMethod(Command command) {
    Method handlerMethod = commandHandlersMethod.get(command.getType());
    if (handlerMethod != null) {
      try {
        handlerMethod.invoke(commandService, command);
      } catch (Exception e) {
        throw new RuntimeException("Failed to invoke handler for command: " + command.getType(), e);
      }
    } else {
      throw new IllegalArgumentException("No handler found for command type: " + command.getType());
    }
  }

  public Object dispatchQuery(Query query) {
    Method handlerMethod = queryHandlersMethod.get(query.getType());
    if (handlerMethod != null) {
      try {
        return handlerMethod.invoke(queryService, query);
      } catch (Exception e) {
        throw new RuntimeException("Failed to invoke handler for query: " + query.getType(), e);
      }
    } else {
      throw new IllegalArgumentException("No handler found for query type: " + query.getType());
    }
  }

  public void publishEvent(Event event) {
    Method handlerMethod = eventHandlersMethod.get(event.getType());
    if (handlerMethod != null) {
      try {
        handlerMethod.invoke(eventService, event);
      } catch (Exception e) {
        throw new RuntimeException("Failed to invoke handler for event: " + event.getType(), e);
      }
    } else {
      throw new IllegalArgumentException("No handler found for event type: " + event.getType());
    }
  }

  @Override
  public void dispatch(Command command) {
    dispatchMethod(command);
  }

  @Override
  public Object dispatch(Query query) {
    return dispatchQuery(query);
  }

  @Override
  public void publish(Event event) {
    publishEvent(event);
  }
}
