package com.e205.domain.member.service;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractRedisTestContainer {

  protected static final String REDIS_IMAGE = "redis:7.0.8-alpine";
  protected static final int REDIS_PORT = 6379;
  protected static GenericContainer<?> redis;

  @BeforeAll
  static void startRedisContainer() throws InterruptedException {
    if (redis == null) {
      redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
          .withExposedPorts(REDIS_PORT);
      redis.start();

      System.setProperty("spring.data.redis.host", redis.getHost());
      System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(REDIS_PORT)));
    }
  }
}