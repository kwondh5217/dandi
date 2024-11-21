package com.e205.cdc;

import org.springframework.stereotype.Component;

@Component
public class RedisEventChannel implements EventChannel{

  @Override
  public void publish(String topic, Object event) {

  }
}
