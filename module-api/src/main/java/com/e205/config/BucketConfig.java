package com.e205.config;

import static java.time.Duration.ofSeconds;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class BucketConfig {

  private final RedisClient redisClient;

  @Bean
  public LettuceBasedProxyManager lettuceBasedProxyManager() {
    StatefulRedisConnection<String, byte[]> connect = redisClient.connect(
        RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    return Bucket4jLettuce.casBasedBuilder(connect)
        .expirationAfterWrite(
            ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(ofSeconds(10)))
        .build();
  }

  @Bean
  public BucketConfiguration bucketConfiguration() {
    return BucketConfiguration.builder()
        .addLimit(limit -> limit.capacity(1).refillGreedy(1, ofSeconds(1)))
        .build();
  }
}
