package com.perfact.be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class AuthResponseDto {
  @Getter
  @AllArgsConstructor
  @Schema(description = "로그인 응답 DTO")
  public static class LoginResponse {

    @Schema(description = "유저의 고유 ID", example = "1")
    private Long userId;

    @Schema(description = "유저 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String refreshToken;
  }

  @Getter
  @AllArgsConstructor
  @Schema(description = "엑세스 토큰 재생성 응답 DTO")
  public static class TokenResponse {
    @Schema(description = "새로 발급된 JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String accessToken;
  }

}
