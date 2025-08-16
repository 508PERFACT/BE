package com.perfact.be.domain.auth.service;

import com.perfact.be.domain.auth.dto.AuthResponseDto;
import com.perfact.be.domain.auth.dto.AuthResponseDto.LoginResponse;
import com.perfact.be.domain.auth.dto.AuthResponseDto.TokenResponse;
import com.perfact.be.domain.auth.dto.NaverTokenResponse;
import com.perfact.be.domain.auth.dto.NaverUserInfoResponse;
import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.auth.exception.AuthHandler;
import com.perfact.be.domain.auth.exception.status.AuthErrorStatus;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.service.UserService;
import com.perfact.be.global.jwt.JwtProvider;
import jakarta.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final WebClient webClient;
  private final UserService userService;
  private final JwtProvider jwtProvider;

  @Resource(name = "rtRedisTemplate")
  private RedisTemplate<String, String> redisTemplate;

  @Value("${naver.client-id}")
  private String clientId;

  @Value("${naver.client-secret}")
  private String clientSecret;

  @Value("${naver.redirect-uri}")
  private String redirectUri;

  @Override
  public AuthResponseDto.LoginResponse socialLogin(String code, String state) {
    String naverAccessToken = getAccessToken(code, state);
    NaverUserProfile profile = getUserProfile(naverAccessToken);
    User user = userService.findOrCreateUser(profile);
    String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getSocialId());

    String uuid = UUID.randomUUID().toString();
    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getSocialId(), uuid);

    String redisKey = "RT:" + user.getSocialId() + ":" + uuid;
    redisTemplate.opsForValue().set(redisKey, refreshToken, 3, TimeUnit.HOURS);

    return new AuthResponseDto.LoginResponse(user.getId(), user.getEmail(), accessToken, refreshToken);
  }

  @Override
  public TokenResponse refreshAccessToken(User loginUser, String refreshToken) {
    // 1. 토큰 유효성 검증 (서명, 만료, 타입)
    jwtProvider.validateRefreshToken(refreshToken);

    validateTokenUserMatch(refreshToken, loginUser);

    String redisKey = buildRedisKeyFromToken(refreshToken);
    String storedToken = redisTemplate.opsForValue().get(redisKey);

    if (storedToken == null || !storedToken.equals(refreshToken)) {
      throw new AuthHandler(AuthErrorStatus.INVALID_REFRESH_TOKEN);
    }

    // AccessToken 재발급 TODO : RTR 은 후순위..
    String newAccessToken = jwtProvider.generateAccessToken(loginUser.getId(), loginUser.getSocialId());
    return new TokenResponse(newAccessToken);
  }

  @Override
  public void logout(User loginUser, String refreshToken) {
    jwtProvider.validateRefreshToken(refreshToken);
    validateTokenUserMatch(refreshToken, loginUser);

    String redisKey = buildRedisKeyFromToken(refreshToken);
    redisTemplate.delete(redisKey);
  }

  @Override
  public LoginResponse guestLogin() {
    String deviceUuid =  UUID.randomUUID().toString();
    User guestUser = userService.createGuestUser(deviceUuid);

    String accessToken = jwtProvider.generateAccessToken(guestUser.getId(), deviceUuid);

    String rtUuid = UUID.randomUUID().toString();
    String refreshToken = jwtProvider.generateRefreshToken(guestUser.getId(), deviceUuid, rtUuid);

    String redisKey = "RT:" + deviceUuid + ":" + rtUuid;
    redisTemplate.opsForValue().set(redisKey, refreshToken, 3, TimeUnit.HOURS);
    return new AuthResponseDto.LoginResponse(guestUser.getId(), guestUser.getEmail(), accessToken, refreshToken);
  }


  private String getAccessToken(String code, String state) {
    NaverTokenResponse tokenResponse = webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .scheme("https")
            .host("nid.naver.com")
            .path("/oauth2.0/token")
            .queryParam("grant_type", "authorization_code")
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("code", code)
            .queryParam("state", state)
            .build()
        )
        .retrieve()
        .onStatus(
            status -> status.isError(),
            response -> Mono.error(new AuthHandler(AuthErrorStatus.NAVER_TOKEN_REQUEST_FAILED))
        )
        .bodyToMono(NaverTokenResponse.class)
        .block();

    if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
      throw new AuthHandler(AuthErrorStatus.NAVER_TOKEN_REQUEST_FAILED);
    }

    return tokenResponse.getAccess_token();
  }

  private NaverUserProfile getUserProfile(String accessToken) {
    NaverUserInfoResponse userInfoResponse = webClient
        .get()
        .uri("https://openapi.naver.com/v1/nid/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .retrieve()
        .onStatus(
            status -> status.isError(),
            response -> Mono.error(new AuthHandler(AuthErrorStatus.NAVER_USERINFO_REQUEST_FAILED))
        )
        .bodyToMono(NaverUserInfoResponse.class)
        .block();

    if (userInfoResponse == null || userInfoResponse.getResponse() == null) {
      throw new AuthHandler(AuthErrorStatus.NAVER_USERINFO_REQUEST_FAILED);
    }

    return userInfoResponse.getResponse();
  }

  private void validateTokenUserMatch(String refreshToken, User loginUser) {
    Long userIdFromToken = jwtProvider.getUserIdFromToken(refreshToken);
    if (!userIdFromToken.equals(loginUser.getId())) {
      throw new AuthHandler(AuthErrorStatus.USER_TOKEN_MISMATCH);
    }
  }

  private String buildRedisKeyFromToken(String refreshToken) {
    String uuid = jwtProvider.getUuidFromToken(refreshToken);
    String socialId = jwtProvider.getSocialIdFromToken(refreshToken);
    return "RT:" + socialId + ":" + uuid;
  }
}
