package com.e205.cdc;

public interface EventChannel {
  void publish(String topic, Object event);
}
