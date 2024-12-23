package com.e205.cdc;

import com.e205.service.Notifier;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FailedEventProcessor {

  private final Notifier notifier;
  private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
  private final String NOTI_DLQ_STREAM = "notification:dlq";

  public FailedEventProcessor(RedisTemplate<String, Object> redisTemplate, Notifier notifier) {
    this.notifier = notifier;
    redisTemplate.opsForStream().createGroup(NOTI_DLQ_STREAM, "consumer-group");
    RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
    StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions =
        StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofMillis(100)).build();
    this.container = StreamMessageListenerContainer.create(connectionFactory, containerOptions);
    this.container.start();
    this.container.receive(Consumer.from("consumer-group", "consumer"),
        StreamOffset.create(NOTI_DLQ_STREAM, ReadOffset.lastConsumed()),
        message -> {
          try {
            Map<String, String> messageBody = message.getValue();

            String deviceToken = messageBody.get("deviceToken");
            String title = messageBody.get("title");
            String body = messageBody.get("body");

            this.notifier.notify(deviceToken, title, body);
            redisTemplate.opsForStream().acknowledge(NOTI_DLQ_STREAM, "consumer-group", message.getId());
          } catch (Exception e) {
            log.warn("failed to acknowledge notification", e);
          }
        });
  }
}
