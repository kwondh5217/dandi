package com.e205.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
@Profile("test")
public class EmbeddedRedisConfig {

  private static final String HOST = "localhost";
  private static final int PORT = 6377;

  private RedisServer redisServer;

  @PostConstruct
  public void startRedis() throws IOException {
    redisServer = RedisServer.newRedisServer().port(PORT).build();
    if(!redisServer.isActive()) {
      redisServer.start();
      log.info("레디스 서버 시작 성공");
    }
  }

  @PreDestroy
  public void stopRedis() throws IOException {
    this.redisServer.stop();
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress("redis://" + HOST + ":" + PORT);
    return Redisson.create(config);
  }
}