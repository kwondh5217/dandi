package com.e205.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Value("${app.push.waitTime:100}")
  private int waitTime;

  @Value("${app.push.computeTime:10}")
  private int computeTime;

  @Bean(name = "fcmPushTaskExecutor")
  public Executor fcmPushTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    int coreCount = Runtime.getRuntime().availableProcessors();
    int optimalThreadCount = coreCount * (1 + waitTime / computeTime);

    executor.setCorePoolSize(optimalThreadCount);
    executor.setMaxPoolSize(optimalThreadCount * 2);
    executor.setQueueCapacity(200);
    executor.setThreadNamePrefix("FCM-Push-Executor-");

    executor.initialize();
    return executor;
  }
}