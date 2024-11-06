package com.e205.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtProviderTest {

  @Autowired
  private JwtProvider jwtProvider;

  @Value("${jwt.secret-key}")
  private String key;

  private SecretKey secretKey;

  @BeforeEach
  void setUp() {
    secretKey = getSecretKey(key);
  }

  @Test
  void 토큰_유효성_검사_테스트() {
    // given
    Integer memberId = 1;
    String token = jwtProvider.generateAccessToken(memberId);

    // when
    boolean isValid = jwtProvider.verifyToken(token);

    // then
    assertThat(isValid).isTrue();
  }

  @Test
  void 만료된_토큰_검사_테스트() {
    // given
    String expiredToken = Jwts.builder()
        .setIssuer("DanDi")
        .setSubject("access-token")
        .claim("memberId", 1)
        .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1시간 전 발행
        .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 30)) // 30분 전 만료
        .signWith(secretKey)
        .compact();

    // when
    boolean isExpired = jwtProvider.isExpired(expiredToken);

    // then
    assertThat(isExpired).isTrue();
  }

  @Test
  void 토큰에서_memberId_추출_테스트() {
    // given
    Integer memberId = 1;
    String token = jwtProvider.generateAccessToken(memberId);

    // when
    Integer extractedMemberId = jwtProvider.getMemberId(token);

    // then
    assertThat(extractedMemberId).isEqualTo(memberId);
  }

  private SecretKey getSecretKey(String secret) {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
}
