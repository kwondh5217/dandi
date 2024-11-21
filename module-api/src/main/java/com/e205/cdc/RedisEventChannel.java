package com.e205.cdc;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisEventChannel implements EventChannel{

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void publish(String topic, Object event) {
    try {
      Map<String, Object> eventMap = Map.of("payload", event);

      MapRecord<String, String, Object> record = MapRecord.create("topic", eventMap);

      RecordId recordId = redisTemplate.opsForStream().add(record);

    } catch (Exception e) {
      System.err.println("Failed to publish event to Redis Stream: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
