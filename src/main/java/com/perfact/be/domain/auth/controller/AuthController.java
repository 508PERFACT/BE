package com.perfact.be.domain.auth.controller;

import com.perfact.be.domain.auth.dto.AuthRequestDto;
import com.perfact.be.domain.auth.dto.AuthResponseDto;
import com.perfact.be.domain.auth.exception.status.AuthSuccessStatus;
import com.perfact.be.domain.auth.service.AuthService;
import com.perfact.be.domain.user.entity.User;
import com.perfact.be.global.apiPayload.ApiResponse;
import com.perfact.be.global.resolver.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  @Operation(
      summary = "네이버 소셜 로그인",
      description = "네이버 로그인 성공 후 전달된 code, state 파라미터를 이용해 로그인 처리를 수행합니다."
  )
  @GetMapping(value = "/social-login/naver", produces = "application/json")
  public ApiResponse<AuthResponseDto.LoginResponse> socialLogin(
      @Parameter(
          description = "네이버 인증 서버에서 전달한 코드 (Access Token을 요청할 때 사용됨)",
          required = true,
          example = "sfasdasdasdasdasdasd"
      )
      @RequestParam String code,
      @Parameter(
          description = "요청 위조를 방지하기 위한 상태값 (CSRF 보호용)",
          required = true,
          example = "fasdds"
      )
      @RequestParam String state) {
    AuthResponseDto.LoginResponse response = authService.socialLogin(code, state);
    return ApiResponse.of(AuthSuccessStatus.SOCIAL_LOGIN_SUCCESS, response);
  }

  @Operation(
      summary = "게스트 로그인",
      description = "UUID 기반 게스트 계정을 생성하고 엑세스/리프레시 토큰을 발급합니다."
  )
  @PostMapping(value = "/guest-login", produces = "application/json")
  public ApiResponse<AuthResponseDto.LoginResponse> guestLogin(
  ) {
    AuthResponseDto.LoginResponse response = authService.guestLogin();
    return ApiResponse.of(AuthSuccessStatus.GUEST_LOGIN_SUCCESS, response);
  }


  @Operation(
      summary = "엑세스 토큰 재발급",
      description = "리프레시 토큰을 이용해 엑세스 토큰을 재발급합니다."
  )
  @PostMapping(value = "/refresh", produces = "application/json")
  public ApiResponse<AuthResponseDto.TokenResponse> refreshAccessToken(
      @Valid @RequestBody AuthRequestDto.RefreshTokenRequest request,
      @CurrentUser @Parameter(hidden = true)User loginUser) {
    AuthResponseDto.TokenResponse response = authService.refreshAccessToken(loginUser, request.getRefreshToken());
    return ApiResponse.of(AuthSuccessStatus.AT_REFRESH_SUCCESS, response);
  }

  @Operation(
      summary = "로그아웃",
      description = "현재 로그인한 사용자의 리프레시 토큰을 삭제하고 로그아웃 처리합니다."
  )
  @PostMapping(value = "/logout", produces = "application/json")
  public ApiResponse<Void> logout(
      @Valid @RequestBody AuthRequestDto.RefreshTokenRequest request,
      @CurrentUser @Parameter(hidden = true) User loginUser ) {
    authService.logout(loginUser, request.getRefreshToken());
    return ApiResponse.of(AuthSuccessStatus.LOGOUT_SUCCESS, null);
  }




}
