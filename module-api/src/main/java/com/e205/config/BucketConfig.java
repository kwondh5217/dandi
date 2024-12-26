package com.e205.config;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

import io.github.bucket4j.BucketConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class BucketConfig {

  public BucketConfiguration createBucketConfiguration(HttpMethod method) {
    if(method == HttpMethod.POST) {
      return BucketConfiguration.builder()
          .addLimit(limit -> limit.capacity(1).refillGreedy(1, ofMillis(500)))
          .build();
    }

    return BucketConfiguration.builder()
        .addLimit(limit -> limit.capacity(10).refillGreedy(10, ofSeconds(1)))
        .build();
  }
}
