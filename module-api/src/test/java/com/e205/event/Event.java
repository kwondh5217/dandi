package com.e205.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Entity
public class Event {

  @Id
  private String eventId;
  private String status;
  private String eventType;

  public Event() {
  }

  public Event(Object event) {
    try {
      Method getEventId = event.getClass().getDeclaredMethod("getEventId");
      Method getType = event.getClass().getDeclaredMethod("getType");
      getType.setAccessible(true);
      this.eventId = (String) getEventId.invoke(event);
      this.eventType = (String) getType.invoke(event);
      this.status = "PENDING";
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public String getEventId() {
    return eventId;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }
}
