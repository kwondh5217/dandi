package com.e205.auth.jwt.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtRepositoryTest {

  private static final String KEY = "token:";
  private static final String HASH_KEY = "token";

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private HashOperations hashOps;

  @InjectMocks
  private JwtRepository jwtRepository;

  private Integer memberId;
  private String refreshToken;

  @BeforeEach
  void setUp() {
    memberId = 1;
    refreshToken = "sampleRefreshToken";
    ReflectionTestUtils.setField(jwtRepository, "expireTime", 1000L);
  }

  @Test
  void 토큰_저장_테스트() {
    // given
    given(redisTemplate.opsForHash()).willReturn(hashOps);

    // when
    jwtRepository.saveRefreshToken(memberId, refreshToken);

    // then
    verify(hashOps, times(1)).put(KEY + memberId, HASH_KEY, refreshToken);
  }

  @Test
  void 토큰_조회_테스트() {
    // given
    given(redisTemplate.opsForHash()).willReturn(hashOps);
    given(hashOps.get(KEY + memberId, HASH_KEY)).willReturn(refreshToken);

    // when
    String findToken = jwtRepository.getRefreshToken(memberId);

    // then
    assertNotNull(findToken);
    assertEquals(refreshToken, findToken);
  }

  @Test
  void 토큰_삭제_테스트() {
    // when
    jwtRepository.deleteRefreshTokenByMemberId(memberId);

    // then
    verify(redisTemplate, times(1)).delete(KEY + memberId);
  }

  @Test
  void 토큰_TTL_설정_테스트() {
    // given
    given(redisTemplate.opsForHash()).willReturn(hashOps);

    // when
    jwtRepository.saveRefreshToken(memberId, refreshToken);

    // then
    verify(redisTemplate, times(1))
        .expire(eq(KEY + memberId), any(Long.class), eq(TimeUnit.SECONDS));
  }

  @ParameterizedTest
  @CsvSource({
      "sampleRefreshToken, true",
      "null, false"
  })
  void 토큰_존재_여부_테스트(String storedToken, boolean expectedExists) {
    // given
    String token = null;
    if (!"null".equals(storedToken)) {
      token = storedToken;
    }

    given(redisTemplate.opsForHash()).willReturn(hashOps);
    given(hashOps.get(KEY + memberId, HASH_KEY)).willReturn(token);

    // when
    boolean exists = jwtRepository.existByMemberId(memberId);

    // then
    assertThat(exists).isEqualTo(expectedExists);
  }
}
