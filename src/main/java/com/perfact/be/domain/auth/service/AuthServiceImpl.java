package com.perfact.be.domain.auth.service;

import com.perfact.be.domain.auth.dto.AuthResponseDto;
import com.perfact.be.domain.auth.dto.NaverTokenResponse;
import com.perfact.be.domain.auth.dto.NaverUserInfoResponse;
import com.perfact.be.domain.auth.dto.NaverUserProfile;
import com.perfact.be.domain.auth.exception.AuthHandler;
import com.perfact.be.domain.auth.exception.status.AuthErrorStatus;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.domain.user.service.UserService;
import com.perfact.be.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${naver.client-id}")
  private String clientId;

  @Value("${naver.client-secret}")
  private String clientSecret;

  @Value("${naver.redirect-uri}")
  private String redirectUri;

  @Override
  public AuthResponseDto.LoginResponse socialLogin(String code, String state) {
    String accessToken = getAccessToken(code, state);
    NaverUserProfile profile = getUserProfile(accessToken);
    User user = userService.findOrCreateUser(profile);
    String token = jwtProvider.generateToken(user.getId(), user.getEmail());

    return new AuthResponseDto.LoginResponse(user.getId(), user.getEmail(), token);
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
}
