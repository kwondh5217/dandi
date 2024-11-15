package com.e205.auth.rateLimit;

import com.e205.config.BucketConfig;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RateLimiterService {

  private final BucketConfig bucketConfig;

  public boolean isRequestAllowed(final String memberId, final HttpMethod method, final String path) {
    String key = "rate-limiter:" + memberId + ":" + method.toString() + ":" + path;

    Bucket bucket = bucketConfig.lettuceBasedProxyManager().builder()
        .build(key, () -> bucketConfig.bucketConfiguration());

    return bucket.tryConsume(1);
  }
}
