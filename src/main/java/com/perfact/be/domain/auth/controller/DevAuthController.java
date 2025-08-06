package com.perfact.be.domain.auth.controller;

import com.perfact.be.domain.auth.exception.status.AuthSuccessStatus;
import com.perfact.be.domain.auth.service.DevAuthService;
import com.perfact.be.domain.user.exception.status.UserSuccessStatus;
import com.perfact.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Tag(name="Dev", description = "로컬 개발용 인증 관련 API")
@Profile("dev")
@RestController
@RequestMapping("/dev/auth")
@RequiredArgsConstructor
public class DevAuthController {

  private final DevAuthService devAuthService;

  @Operation(summary = "개발용 유저 + 토큰 발급", description = "임시 유저를 생성하고 JWT 토큰을 발급합니다.")
  @PostMapping("/token")
  public ApiResponse<String> generateDevToken() {
    String token = devAuthService.createFakeUserAndGetToken();
    return ApiResponse.of(AuthSuccessStatus.DEV_TOKEN_ISSUED, token);
  }
}
