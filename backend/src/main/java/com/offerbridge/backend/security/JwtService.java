package com.offerbridge.backend.security;

import com.offerbridge.backend.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
  private final AppProperties properties;

  public JwtService(AppProperties properties) {
    this.properties = properties;
  }

  public String issueAccessToken(Long userId) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(properties.getJwt().getAccessExpireSeconds());
    return Jwts.builder()
      .issuer(properties.getJwt().getIssuer())
      .subject(String.valueOf(userId))
      .id(UUID.randomUUID().toString())
      .issuedAt(Date.from(now))
      .expiration(Date.from(exp))
      .signWith(secretKey())
      .compact();
  }

  public String issueRefreshToken(Long userId) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(properties.getJwt().getRefreshExpireSeconds());
    return Jwts.builder()
      .issuer(properties.getJwt().getIssuer())
      .subject(String.valueOf(userId))
      .id(UUID.randomUUID().toString())
      .issuedAt(Date.from(now))
      .expiration(Date.from(exp))
      .claim("typ", "refresh")
      .signWith(secretKey())
      .compact();
  }

  public Long parseUserId(String token) {
    Claims claims = Jwts.parser()
      .verifyWith(secretKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
    return Long.parseLong(claims.getSubject());
  }

  public Instant parseExpiry(String token) {
    Claims claims = Jwts.parser()
      .verifyWith(secretKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
    return claims.getExpiration().toInstant();
  }

  private SecretKey secretKey() {
    return Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
  }
}
