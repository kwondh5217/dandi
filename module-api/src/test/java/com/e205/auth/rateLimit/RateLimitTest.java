package com.e205.auth.rateLimit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;

@SpringBootTest
public class RateLimitTest {

  @Autowired
  private RateLimiterService rateLimiterService;

  @Test
  public void getMethodAllows10RequestOneSecond() throws InterruptedException {
    String memberId = "memberId";
    HttpMethod method = HttpMethod.GET;
    String path = "/noti/test";

    AtomicInteger successCount = new AtomicInteger(0);
    CountDownLatch countDownLatch = new CountDownLatch(20);
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    for(int i = 0; i < 20; i++) {
      executorService.execute(() -> {
        boolean requestAllowed = this.rateLimiterService.isRequestAllowed(memberId, method, path);
        if(requestAllowed) {
          successCount.incrementAndGet();
        }
        countDownLatch.countDown();
      });
    }

    countDownLatch.await();

    Assertions.assertThat(successCount.get()).isEqualTo(10);
  }

  @Test
  public void postMethodAllows1Request5MilliSeconds() throws InterruptedException {
    String memberId = "memberId";
    HttpMethod method = HttpMethod.POST;
    String path = "/noti/test";

    AtomicInteger successCount = new AtomicInteger(0);
    CountDownLatch countDownLatch = new CountDownLatch(5);
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    for(int i = 0; i < 5; i++) {
      executorService.execute(() -> {
        boolean requestAllowed = this.rateLimiterService.isRequestAllowed(memberId, method, path);
        if(requestAllowed) {
          successCount.incrementAndGet();
        }
        countDownLatch.countDown();
      });
    }

    countDownLatch.await();

    Assertions.assertThat(successCount.get()).isEqualTo(1);
  }
}
