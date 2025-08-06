package com.perfact.be.global.jwt;

import com.perfact.be.domain.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long accessTokenExpirationMillis;

  private final long refreshTokenExpirationMillis = 3 * 60 * 60 * 1000L;

  private Key key;

  private final CustomUserDetailsService customUserDetailsService;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(Long userId, String socialId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + accessTokenExpirationMillis);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("socialId", socialId)
        .claim("type", "ACCESS")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Long userId, String socialId, String uuid) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMillis);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("socialId", socialId)
        .claim("uuid", uuid)
        .claim("type", "REFRESH")
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public void validateAccessToken(String token) {
    validateTokenWithType(token, "ACCESS");
  }

  public void validateRefreshToken(String token) {
    validateTokenWithType(token, "REFRESH");
  }

  private void validateTokenWithType(String token, String expectedType) {
    try {
      Claims claims = parseClaims(token);
      String type = claims.get("type", String.class);
      if (!expectedType.equals(type)) {
        throw new BadCredentialsException("유효하지 않은 토큰 타입입니다: " + type);
      }
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT 인증 실패: {}", e.getMessage());
      throw new BadCredentialsException("유효하지 않은 JWT", e);
    }
  }

  public Authentication getAuthentication(String token) {
    String userId = getUserIdFromToken(token).toString();
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  public Long getUserIdFromToken(String token) {
    return Long.parseLong(parseClaims(token).getSubject());
  }

  public String getSocialIdFromToken(String token) {
    return parseClaims(token).get("socialId", String.class);
  }


  public String getUuidFromToken(String token) {
    return parseClaims(token).get("uuid", String.class);
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
