package com.e205.event;

import com.e205.events.Event;
import java.time.LocalDateTime;

public class TestEvent implements Event {

  private String testId;
  private String title;
  private String content;
  private LocalDateTime time = LocalDateTime.now();

  public TestEvent() {
  }

  public TestEvent(String testId, String title, String content) {
    this.testId = testId;
    this.title = title;
    this.content = content;
  }

  @Override
  public String getType() {
    return "testEvent";
  }
}
