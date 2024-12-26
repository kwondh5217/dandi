package com.e205.auth.rateLimit;

import com.e205.config.BucketConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RateLimiterService {

  private final LettuceBasedProxyManager lettuceBasedProxyManager;
  private final BucketConfig bucketConfig;

  public boolean isRequestAllowed(final String memberId, final HttpMethod method, final String path) {
    String key = "rate-limiter:" + memberId + ":" + method.toString() + ":" + path;

    io.github.bucket4j.BucketConfiguration bucketConfig = this.bucketConfig.createBucketConfiguration(
        method);

    Bucket bucket = this.lettuceBasedProxyManager.builder()
        .build(key, () -> bucketConfig);

    return bucket.tryConsume(1);
  }
}
