package com.e205.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

  private static final String ISSUER = "DanDi";

  @Value("${jwt.secret-key}")
  private String key;

  @Value("${jwt.access-token-expire-time}")
  private Long accessExpireTime;

  @Value("${jwt.refresh-token-expire-time}")
  private Long refreshExpireTime;

  public String generateAccessToken(Integer memberId) {
    return Jwts.builder()
        .setIssuer(ISSUER)
        .setSubject("access-token")
        .claim("memberId", memberId)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessExpireTime * 1000))
        .signWith(getSecretKey(key))
        .compact();
  }

  public String generateRefreshToken(Integer memberId) {
    return Jwts.builder()
        .setIssuer(ISSUER)
        .setSubject("refresh-token")
        .claim("memberId", memberId)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshExpireTime * 1000))
        .signWith(getSecretKey(key))
        .compact();
  }

  public Integer getMemberId(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSecretKey(key))
        .build()
        .parseClaimsJws(token)
        .getBody()
        .get("memberId", Integer.class);
  }

  public boolean verifyToken(String token) {
    try {
      Jwts.parserBuilder()
          .build()
          .isSigned(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isNotExpired(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getSecretKey(key))
          .build()
          .parseClaimsJwt(token)
          .getBody()
          .getExpiration()
          .before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private SecretKey getSecretKey(String secret) {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
}