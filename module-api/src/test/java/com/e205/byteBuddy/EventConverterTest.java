package com.e205.byteBuddy;

import static org.assertj.core.api.Assertions.*;

import com.e205.event.TestEvent;
import com.e205.events.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventConverterTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void convertToEventEvent() throws JsonProcessingException {
    EventConverter eventConverter = new EventConverter(this.objectMapper);
    EventConverter.eventClasses.put("testEvent", TestEvent.class);
    TestEvent testEvent = new TestEvent("test", "title", "content");
    OutboxEvent outboxEvent = eventConverter.toOutboxEvent(testEvent);

    Event event = eventConverter.toEvent(outboxEvent);

    assertThat(event).isNotNull();
  }

  @Test
  void toOutBoxEvent() throws JsonProcessingException {
    EventConverter eventConverter = new EventConverter(this.objectMapper);
    TestEvent testEvent = new TestEvent("test", "title", "content");

    OutboxEvent outboxEvent = eventConverter.toOutboxEvent(testEvent);

    assertThat(outboxEvent).isNotNull();
  }

}