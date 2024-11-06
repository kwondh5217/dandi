package com.e205.byteBuddy;

import static net.bytebuddy.asm.Advice.FieldValue;
import static net.bytebuddy.asm.Advice.OnMethodEnter;
import static net.bytebuddy.asm.Advice.to;

import com.e205.events.Event;
import java.lang.instrument.Instrumentation;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class EventAgent {

  public static void premain(String agentArgs, Instrumentation inst) {
    log.info("start premain");
    new AgentBuilder.Default()
        .type(ElementMatchers.isSubTypeOf(Event.class))
        .transform((builder, type, classLoader, module, protectionDomain) -> {
          log.info("save transformedClass : {}, {}", type.getName(), type.getClass());
          EventConverter.eventClasses.put(type.getName(), type.getClass());
          return builder
              .defineField("eventId", String.class, Visibility.PRIVATE)
              .defineField("status", EventStatus.class, Visibility.PRIVATE)
              .defineMethod("getEventId", String.class, Visibility.PUBLIC)
              .intercept(FieldAccessor.ofField("eventId"))
              .defineMethod("setEventId", void.class, Visibility.PUBLIC)
              .withParameter(String.class)
              .intercept(FieldAccessor.ofField("eventId"))
              .defineMethod("getStatus", EventStatus.class, Visibility.PUBLIC)
              .intercept(FieldAccessor.ofField("status"))
              .defineMethod("setStatus", void.class, Visibility.PUBLIC)
              .withParameter(EventStatus.class)
              .intercept(FieldAccessor.ofField("status"))
              .visit(to(EventIdInitializationAdvice.class).on(
                  ElementMatchers.named("getEventId")));
        })
        .installOn(inst);
  }

  public static class EventIdInitializationAdvice {

    @OnMethodEnter
    public static void initializeEventId(
        @FieldValue(value = "eventId", readOnly = false) String eventId,
        @FieldValue(value = "status", readOnly = false) EventStatus status) {
      if (!StringUtils.hasText(eventId)) {
        eventId = UUID.randomUUID().toString();
      }
      if (ObjectUtils.isEmpty(status)) {
        status = EventStatus.PENDING;
      }
    }
  }
}
