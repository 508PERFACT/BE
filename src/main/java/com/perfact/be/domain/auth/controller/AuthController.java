package com.perfact.be.domain.auth.controller;

import com.perfact.be.domain.auth.dto.AuthResponseDto;
import com.perfact.be.domain.auth.exception.status.AuthSuccessStatus;
import com.perfact.be.domain.auth.service.AuthService;
import com.perfact.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Tag(name="Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  @Operation(
      summary = "네이버 로그인 콜백",
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



}
