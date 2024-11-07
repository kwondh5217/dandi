package com.e205.auth.jwt.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtRepository {

  @Value("${jwt.refresh-token-expire-time}")
  private Long expireTime;

  private static final String KEY = "token:";
  private static final String HASH_KEY = "token";

  private final RedisTemplate<String, String> redisTemplate;

  public void saveRefreshToken(Integer memberId, String refreshToken) {
    hashOps().put(getTokenKey(memberId), HASH_KEY, refreshToken);
    redisTemplate.expire(getTokenKey(memberId), expireTime, TimeUnit.SECONDS);
  }

  public void deleteRefreshTokenByMemberId(Integer memberId) {
    redisTemplate.delete(getTokenKey(memberId));
  }

  public String getRefreshToken(Integer memberId) {
    return hashOps().get(getTokenKey(memberId), HASH_KEY);
  }

  public boolean existByMemberId(Integer memberId) {
    String token = hashOps().get(getTokenKey(memberId), HASH_KEY);
    return token != null && !token.isEmpty();
  }

  private String getTokenKey(Integer memberId) {
    return KEY + memberId;
  }

  private HashOperations<String, String, String> hashOps() {
    return redisTemplate.opsForHash();
  }
}
