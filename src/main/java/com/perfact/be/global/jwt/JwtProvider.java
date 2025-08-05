package com.perfact.be.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long expirationMillis;
  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
  }


  public String generateToken(Long userId, String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMillis);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("email", email)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰에서 사용자 ID 추출
  public Long getUserIdFromToken(String token) {
    Claims claims = parseClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // 내부에서 사용 - 토큰 파싱
  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
